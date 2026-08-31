package com.kallista.core.geojson.services.impl;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobBuilder;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.ScheduledJobInfo;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kallista.core.geojson.constants.GeoJsonPipelineConstants;
import com.kallista.core.geojson.services.GeoJsonJobProducer;

@Component(service = GeoJsonJobProducer.class)
public class GeoJsonJobProducerImpl implements GeoJsonJobProducer {

    private static final Logger LOG = LoggerFactory.getLogger(GeoJsonJobProducerImpl.class);

    @Reference
    private JobManager jobManager;

    @Override
    public String newCorrelationId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean enqueue(String assetPath, String uploader, String correlationId, int retryCount, long delayMillis) {
        Map<String, Object> payload = new HashMap<>();
        payload.put(GeoJsonPipelineConstants.JOB_PROP_ASSET_PATH, assetPath);
        payload.put(GeoJsonPipelineConstants.JOB_PROP_UPLOADER, uploader == null ? "unknown" : uploader);
        payload.put(GeoJsonPipelineConstants.JOB_PROP_CORRELATION_ID, correlationId);
        payload.put(GeoJsonPipelineConstants.JOB_PROP_RETRY_COUNT, retryCount);
        payload.put(GeoJsonPipelineConstants.JOB_PROP_SCHEDULED_AT, Instant.now().toString());

        JobBuilder builder = jobManager.createJob(GeoJsonPipelineConstants.JOB_TOPIC).properties(payload);
        boolean created;
        if (delayMillis > 0) {
            Date scheduledAt = new Date(System.currentTimeMillis() + delayMillis);
            ScheduledJobInfo scheduled = builder.schedule().at(scheduledAt).add();
            created = scheduled != null;
        } else {
            Job job = builder.add();
            created = job != null;
        }

        if (!created) {
            LOG.error("failed to create geojson job. assetPath={} correlationId={} retryCount={} delayMillis={}",
                    assetPath, correlationId, retryCount, delayMillis);
            return false;
        }

        LOG.info("job created. assetPath={} correlationId={} retryCount={} delayMillis={} topic={}", assetPath,
                correlationId, retryCount, delayMillis, GeoJsonPipelineConstants.JOB_TOPIC);
        return true;
    }
}
