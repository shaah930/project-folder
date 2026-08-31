package com.kallista.core.geojson.jobs;

import java.io.IOException;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kallista.core.geojson.constants.GeoJsonPipelineConstants;
import com.kallista.core.geojson.models.ProcessingStatus;
import com.kallista.core.geojson.services.AssetReadinessService;
import com.kallista.core.geojson.services.GeoJsonJobProducer;
import com.kallista.core.geojson.services.NotificationService;
import com.kallista.core.geojson.services.StatusTrackingService;
import com.kallista.core.geojson.services.impl.GeoJsonProcessingServiceImpl;

@Designate(ocd = GeoJsonGenerationJobConsumer.Config.class)
@Component(service = JobConsumer.class, property = {
        JobConsumer.PROPERTY_TOPICS + "=" + GeoJsonPipelineConstants.JOB_TOPIC
})
public class GeoJsonGenerationJobConsumer implements JobConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(GeoJsonGenerationJobConsumer.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private StatusTrackingService statusTrackingService;

        @Reference
        private NotificationService notificationService;

        @Reference
        private GeoJsonProcessingServiceImpl geoJsonProcessingService;

        @Reference
        private AssetReadinessService assetReadinessService;

        @Reference
        private GeoJsonJobProducer geoJsonJobProducer;

        private volatile int maxRetries;
        private volatile long initialRetryDelayMillis;
        private volatile double retryBackoffMultiplier;
        private volatile long maxRetryDelayMillis;

        @ObjectClassDefinition(name = "Kallista GeoJSON Job Consumer", description = "Retry and backoff settings for DAM asset processing")
        public @interface Config {

        @AttributeDefinition(name = "Max retries", description = "Maximum retry attempts when asset processing is not complete")
        int maxRetries() default 8;

        @AttributeDefinition(name = "Initial retry delay (ms)", description = "Delay for first retry attempt in milliseconds")
        long initialRetryDelayMillis() default 5000;

        @AttributeDefinition(name = "Retry backoff multiplier", description = "Delay multiplier applied for each retry")
        double retryBackoffMultiplier() default 2.0;

        @AttributeDefinition(name = "Max retry delay (ms)", description = "Upper bound for retry delay in milliseconds")
        long maxRetryDelayMillis() default 120000;
        }

        @Activate
        @Modified
        protected void activate(Config config) {
        this.maxRetries = Math.max(0, config.maxRetries());
        this.initialRetryDelayMillis = Math.max(1000L, config.initialRetryDelayMillis());
        this.retryBackoffMultiplier = Math.max(1.0d, config.retryBackoffMultiplier());
        this.maxRetryDelayMillis = Math.max(this.initialRetryDelayMillis, config.maxRetryDelayMillis());
        }

    @Override
    public JobResult process(Job job) {
        String assetPath = job.getProperty(GeoJsonPipelineConstants.JOB_PROP_ASSET_PATH, String.class);
        String uploader = job.getProperty(GeoJsonPipelineConstants.JOB_PROP_UPLOADER, String.class);
        String correlationId = job.getProperty(GeoJsonPipelineConstants.JOB_PROP_CORRELATION_ID, String.class);
        int retryCount = parseRetryCount(job);

        if (assetPath == null || assetPath.isBlank()) {
            LOG.error("job cancelled because assetPath is missing. correlationId={} retryCount={}", correlationId,
                retryCount);
            return JobResult.CANCEL;
        }

        LOG.info("job received. assetPath={} correlationId={} retryCount={}", assetPath, correlationId, retryCount);

        try (ResourceResolver resolver = getServiceResolver()) {
            Resource assetResource = resolver.getResource(assetPath);
            if (assetResource == null) {
            LOG.warn("asset no longer exists. assetPath={} correlationId={} retryCount={}", assetPath,
                correlationId, retryCount);
                statusTrackingService.updateStatus(assetPath, ProcessingStatus.FAILED, correlationId,
                "CSV asset no longer exists", null);
            notificationService.notifyFailure(nullSafe(uploader), assetPath, "CSV asset no longer exists",
                correlationId);
                return JobResult.CANCEL;
            }

            if (correlationId != null && !statusTrackingService.isCurrentCorrelation(assetPath, correlationId)) {
            LOG.info("stale/duplicate job skipped (correlation mismatch). assetPath={} correlationId={} retryCount={}",
                assetPath, correlationId, retryCount);
            return JobResult.CANCEL;
            }

            if (statusTrackingService.isCompleted(assetPath)) {
            LOG.info("duplicate job skipped because processing is already completed. assetPath={} correlationId={}",
                assetPath, correlationId);
            return JobResult.CANCEL;
            }

            AssetReadinessService.ReadinessResult readiness = assetReadinessService.check(assetPath, resolver);
            if (!readiness.isReady()) {
            LOG.info("asset not ready. assetPath={} correlationId={} retryCount={} reason={}", assetPath,
                correlationId, retryCount, readiness.getReason());

            if (readiness.isAssetMissing()) {
                LOG.warn("asset no longer exists during readiness check. assetPath={} correlationId={}", assetPath,
                    correlationId);
                statusTrackingService.updateStatus(assetPath, ProcessingStatus.FAILED, correlationId,
                    "CSV asset no longer exists", null);
                notificationService.notifyFailure(nullSafe(uploader), assetPath, "CSV asset no longer exists",
                    correlationId);
                return JobResult.CANCEL;
            }

            if (retryCount >= maxRetries) {
                LOG.error("maximum retries reached. assetPath={} correlationId={} retryCount={} reason={}",
                    assetPath, correlationId, retryCount, readiness.getReason());
                statusTrackingService.updateStatus(assetPath, ProcessingStatus.FAILED, correlationId,
                    "Maximum retries reached before asset became ready: " + readiness.getReason(), null);
                notificationService.notifyFailure(nullSafe(uploader), assetPath,
                    "Maximum retries reached before asset became ready", correlationId);
                return JobResult.CANCEL;
            }

            int nextRetryCount = retryCount + 1;
            long delayMillis = computeDelayMillis(nextRetryCount);
            LOG.info("retry attempt scheduled. assetPath={} correlationId={} currentRetry={} nextRetry={} delayMillis={}",
                assetPath, correlationId, retryCount, nextRetryCount, delayMillis);
            boolean created = geoJsonJobProducer.enqueue(assetPath, nullSafe(uploader), correlationId,
                nextRetryCount, delayMillis);
            if (!created) {
                LOG.error("failed to reschedule retry job. assetPath={} correlationId={} nextRetry={}", assetPath,
                    correlationId, nextRetryCount);
                return JobResult.FAILED;
            }
            return JobResult.OK;
            }

            LOG.info("asset ready. assetPath={} correlationId={} readiness={}", assetPath, correlationId,
                readiness.getReason());

            String resolvedUploader = resolveUploader(assetResource, uploader);
            LOG.info("business processing started. assetPath={} correlationId={} uploader={}", assetPath,
                correlationId, resolvedUploader);
            geoJsonProcessingService.process(assetPath, resolvedUploader, correlationId);
            LOG.info("business processing completed. assetPath={} correlationId={} uploader={}", assetPath,
                correlationId, resolvedUploader);
            return JobResult.OK;
        } catch (LoginException ex) {
            LOG.error("unable to obtain service resolver for geojson job. assetPath={} correlationId={}", assetPath,
                    correlationId, ex);
            return JobResult.FAILED;
        } catch (IOException | RuntimeException ex) {
            LOG.error("geojson job failed. assetPath={} correlationId={}", assetPath, correlationId, ex);
            statusTrackingService.updateStatus(assetPath, ProcessingStatus.FAILED, correlationId, ex.getMessage(),
                    null);
            notificationService.notifyFailure(nullSafe(uploader), assetPath, ex.getMessage(), correlationId);
            return JobResult.FAILED;
        }
    }

    private ResourceResolver getServiceResolver() throws LoginException {
        return resourceResolverFactory.getServiceResourceResolver(
                Map.of(ResourceResolverFactory.SUBSERVICE, GeoJsonPipelineConstants.SUBSERVICE_NAME));
    }

    private int parseRetryCount(Job job) {
        Object raw = job.getProperty(GeoJsonPipelineConstants.JOB_PROP_RETRY_COUNT);
        if (raw instanceof Number) {
            return Math.max(0, ((Number) raw).intValue());
        }
        if (raw instanceof String) {
            try {
                return Math.max(0, Integer.parseInt((String) raw));
            } catch (NumberFormatException ex) {
                return 0;
            }
        }
        return 0;
    }

    private long computeDelayMillis(int nextRetryCount) {
        double exponent = Math.max(0, nextRetryCount - 1);
        double computed = initialRetryDelayMillis * Math.pow(retryBackoffMultiplier, exponent);
        long bounded = (long) Math.min(computed, (double) maxRetryDelayMillis);
        return Math.max(initialRetryDelayMillis, bounded);
    }

    private String resolveUploader(Resource assetResource, String fallbackUploader) {
        Resource metadata = assetResource.getChild("jcr:content/metadata");
        if (metadata == null) {
            return nullSafe(fallbackUploader);
        }

        ValueMap map = metadata.getValueMap();
        String creator = map.get("dc:creator", String.class);
        if (creator == null || creator.isBlank()) {
            creator = map.get("jcr:createdBy", String.class);
        }
        return creator == null || creator.isBlank() ? nullSafe(fallbackUploader) : creator;
    }

    private static String nullSafe(String value) {
        return value == null || value.isBlank() ? "unknown" : value;
    }
}