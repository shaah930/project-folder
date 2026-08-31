package com.kallista.core.geojson.listener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChange.ChangeType;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kallista.core.geojson.constants.GeoJsonPipelineConstants;
import com.kallista.core.geojson.models.ProcessingStatus;
import com.kallista.core.geojson.services.GeoJsonJobProducer;
import com.kallista.core.geojson.services.StatusTrackingService;
import com.kallista.core.geojson.services.impl.GeoJsonAssetServiceConfig;

@Designate(ocd = com.kallista.core.geojson.services.impl.GeoJsonAssetServiceConfig.class)
@Component(service = ResourceChangeListener.class, property = {
    ResourceChangeListener.PATHS + "=" + GeoJsonAssetServiceConfig.DEFAULT_DAM_BASE_PATH + GeoJsonPipelineConstants.CSV_INCOMING_PATH,
    ResourceChangeListener.CHANGES + "=ADDED",
    ResourceChangeListener.CHANGES + "=CHANGED"
})
public class CsvUploadResourceChangeListener implements ResourceChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(CsvUploadResourceChangeListener.class);
        private volatile String csvIncomingPath = GeoJsonPipelineConstants.resolvePath(
            GeoJsonAssetServiceConfig.DEFAULT_DAM_BASE_PATH, GeoJsonPipelineConstants.CSV_INCOMING_PATH);

    @Activate
    @Modified
    protected void activate(com.kallista.core.geojson.services.impl.GeoJsonAssetServiceConfig config) {
        
        String basePath = config.damBasePath();
        if (basePath == null || basePath.trim().isEmpty()) {
            basePath = GeoJsonAssetServiceConfig.DEFAULT_DAM_BASE_PATH;
        }
        csvIncomingPath = GeoJsonPipelineConstants.resolvePath(basePath, GeoJsonPipelineConstants.CSV_INCOMING_PATH);
    }

    @Reference
    private GeoJsonJobProducer geoJsonJobProducer;

    @Reference
    private StatusTrackingService statusTrackingService;

    @Override
    public void onChange(List<ResourceChange> changes) {
        Set<String> processedAssets = new HashSet<>();
        for (ResourceChange change : changes) {
            if (change.isExternal()) {
                continue;
            }

            String eventPath = change.getPath();
            String assetPath = normalizeAssetPath(eventPath, csvIncomingPath);
            if (assetPath == null || !isRelevantAssetEvent(eventPath, assetPath)
                    || isMetadataChangedEvent(change, eventPath)) {
                continue;
            }

            // Skip if already processed this asset in this batch
            if (processedAssets.contains(assetPath)) {
                LOG.debug("skipping already-processed asset in this batch: {}", assetPath);
                continue;
            }

            String correlationId = geoJsonJobProducer.newCorrelationId();
            if (!statusTrackingService.tryMarkQueued(assetPath, correlationId)) {
                LOG.info("asset already in-flight; skipping duplicate event. assetPath={} eventPath={}", assetPath,
                        eventPath);
                continue;
            }

            boolean created = geoJsonJobProducer.enqueue(assetPath, "unknown", correlationId, 0, 0);
            if (!created) {
                LOG.error("job creation failed. assetPath={} correlationId={}", assetPath, correlationId);
                statusTrackingService.updateStatus(assetPath, ProcessingStatus.FAILED, correlationId,
                        "Unable to enqueue GeoJSON processing job", null);
                continue;
            }

            processedAssets.add(assetPath);
        }
    }

    private static String normalizeAssetPath(String eventPath, String csvIncomingPath) {
        if (eventPath == null || eventPath.isBlank()) {
            return null;
        }
        String candidate;
        int index = eventPath.indexOf("/jcr:content");
        if (index > 0) {
            candidate = eventPath.substring(0, index);
        } else {
            candidate = eventPath.startsWith(csvIncomingPath) ? eventPath : null;
        }
        if (candidate == null) {
            return null;
        }
        return candidate.toLowerCase().endsWith(".csv") ? candidate : null;
    }

    private static boolean isRelevantAssetEvent(String eventPath, String assetPath) {
        return eventPath.equals(assetPath)
                || eventPath.equals(assetPath + "/jcr:content")
                || eventPath.equals(assetPath + "/jcr:content/metadata");
    }

    private static boolean isMetadataChangedEvent(ResourceChange change, String eventPath) {
        return ChangeType.CHANGED.equals(change.getType()) && eventPath.endsWith("/jcr:content/metadata");
    }
}
