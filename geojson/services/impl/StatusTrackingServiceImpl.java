package com.kallista.core.geojson.services.impl;

import java.time.Instant;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kallista.core.geojson.constants.GeoJsonPipelineConstants;
import com.kallista.core.geojson.models.ProcessingStatus;
import com.kallista.core.geojson.services.StatusTrackingService;

@Component(service = StatusTrackingService.class)
public class StatusTrackingServiceImpl implements StatusTrackingService {

    private static final Logger LOG = LoggerFactory.getLogger(StatusTrackingServiceImpl.class);

    private static final EnumSet<ProcessingStatus> IN_FLIGHT = EnumSet.of(
            ProcessingStatus.UPLOADED,
            ProcessingStatus.VALIDATING,
            ProcessingStatus.GENERATING_GEOJSON,
            ProcessingStatus.SAVING,
            ProcessingStatus.PUBLISHING);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void updateStatus(String assetPath, ProcessingStatus status, String correlationId, String errorSummary,
            String invalidRowsSummary) {
        try (ResourceResolver resolver = getServiceResolver()) {
            Resource metadata = getOrCreateMetadataResource(resolver, assetPath);
            if (metadata == null) {
                LOG.warn("unable to update status for assetPath={} because metadata resource is missing", assetPath);
                return;
            }
            ModifiableValueMap valueMap = metadata.adaptTo(ModifiableValueMap.class);
            valueMap.put(GeoJsonPipelineConstants.STATUS_PROP, status.name());
            valueMap.put(GeoJsonPipelineConstants.STATUS_UPDATED_AT_PROP, Instant.now().toString());
            if (correlationId != null) {
                valueMap.put(GeoJsonPipelineConstants.CORRELATION_ID_PROP, correlationId);
            }
            putOrRemove(valueMap, GeoJsonPipelineConstants.STATUS_ERROR_PROP, errorSummary);
            putOrRemove(valueMap, GeoJsonPipelineConstants.STATUS_INVALID_ROWS_PROP, invalidRowsSummary);
            resolver.commit();
        } catch (LoginException | PersistenceException ex) {
            LOG.error("unable to update geojson status for assetPath={} status={}", assetPath, status, ex);
        }
    }

    @Override
    public void markPublished(String assetPath, String outputPath) {
        try (ResourceResolver resolver = getServiceResolver()) {
            Resource metadata = getOrCreateMetadataResource(resolver, assetPath);
            if (metadata == null) {
                return;
            }
            ModifiableValueMap valueMap = metadata.adaptTo(ModifiableValueMap.class);
            valueMap.put(GeoJsonPipelineConstants.PUBLISHED_AT_PROP, Instant.now().toString());
            valueMap.put(GeoJsonPipelineConstants.OUTPUT_PATH_PROP, outputPath);
            resolver.commit();
        } catch (LoginException | PersistenceException ex) {
            LOG.error("unable to mark published for assetPath={}", assetPath, ex);
        }
    }

    @Override
    public boolean isInFlight(String assetPath) {
        try (ResourceResolver resolver = getServiceResolver()) {
            Resource metadata = resolver.getResource(assetPath + "/jcr:content/metadata");
            if (metadata == null) {
                return false;
            }
            String status = metadata.getValueMap().get(GeoJsonPipelineConstants.STATUS_PROP, String.class);
            ProcessingStatus parsed = parseStatus(status);
            if (parsed == null) {
                return false;
            }
            return IN_FLIGHT.contains(parsed);
        } catch (Exception ex) {
            LOG.warn("unable to read in-flight status for assetPath={}", assetPath, ex);
            return false;
        }
    }

    @Override
    public boolean isCompleted(String assetPath) {
        try (ResourceResolver resolver = getServiceResolver()) {
            Resource metadata = resolver.getResource(assetPath + "/jcr:content/metadata");
            if (metadata == null) {
                return false;
            }
            String status = metadata.getValueMap().get(GeoJsonPipelineConstants.STATUS_PROP, String.class);
            return ProcessingStatus.COMPLETED.equals(parseStatus(status));
        } catch (Exception ex) {
            LOG.warn("unable to read completed status for assetPath={}", assetPath, ex);
            return false;
        }
    }

    @Override
    public boolean tryMarkQueued(String assetPath, String correlationId) {
        try (ResourceResolver resolver = getServiceResolver()) {
            Resource metadata = getOrCreateMetadataResource(resolver, assetPath);
            if (metadata == null) {
                LOG.warn("unable to mark queued for assetPath={} because metadata resource is missing", assetPath);
                return false;
            }

            String currentStatus = metadata.getValueMap().get(GeoJsonPipelineConstants.STATUS_PROP, String.class);
            ProcessingStatus parsed = parseStatus(currentStatus);
            if (parsed != null && IN_FLIGHT.contains(parsed)) {
                return false;
            }

            ModifiableValueMap valueMap = metadata.adaptTo(ModifiableValueMap.class);
            valueMap.put(GeoJsonPipelineConstants.STATUS_PROP, ProcessingStatus.UPLOADED.name());
            valueMap.put(GeoJsonPipelineConstants.STATUS_UPDATED_AT_PROP, Instant.now().toString());
            valueMap.put(GeoJsonPipelineConstants.CORRELATION_ID_PROP, correlationId);
            valueMap.remove(GeoJsonPipelineConstants.STATUS_ERROR_PROP);
            valueMap.remove(GeoJsonPipelineConstants.STATUS_INVALID_ROWS_PROP);
            resolver.commit();
            return true;
        } catch (LoginException | PersistenceException ex) {
            LOG.error("unable to mark queued for assetPath={} correlationId={}", assetPath, correlationId, ex);
            return false;
        }
    }

    @Override
    public boolean isCurrentCorrelation(String assetPath, String correlationId) {
        try (ResourceResolver resolver = getServiceResolver()) {
            Resource metadata = resolver.getResource(assetPath + "/jcr:content/metadata");
            if (metadata == null) {
                return false;
            }
            String currentCorrelation = metadata.getValueMap().get(GeoJsonPipelineConstants.CORRELATION_ID_PROP,
                    String.class);
            return correlationId != null && correlationId.equals(currentCorrelation);
        } catch (Exception ex) {
            LOG.warn("unable to read correlation status for assetPath={}", assetPath, ex);
            return false;
        }
    }

    private ResourceResolver getServiceResolver() throws LoginException {
        return resourceResolverFactory.getServiceResourceResolver(
                Map.of(ResourceResolverFactory.SUBSERVICE, GeoJsonPipelineConstants.SUBSERVICE_NAME));
    }

    private Resource getOrCreateMetadataResource(ResourceResolver resolver, String assetPath) throws PersistenceException {
        Resource metadata = resolver.getResource(assetPath + "/jcr:content/metadata");
        if (metadata != null) {
            return metadata;
        }

        Resource jcrContent = resolver.getResource(assetPath + "/jcr:content");
        if (jcrContent == null) {
            return null;
        }

        Map<String, Object> properties = new HashMap<>();
        properties.put("jcr:primaryType", "nt:unstructured");
        return resolver.create(jcrContent, "metadata", properties);
    }

    private static void putOrRemove(ModifiableValueMap valueMap, String key, String value) {
        if (value == null || value.isBlank()) {
            valueMap.remove(key);
            return;
        }
        valueMap.put(key, value);
    }

    private static ProcessingStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return ProcessingStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}