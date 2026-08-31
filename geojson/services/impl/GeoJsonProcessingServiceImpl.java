package com.kallista.core.geojson.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kallista.core.geojson.constants.GeoJsonPipelineConstants;
import com.kallista.core.geojson.models.ProcessingStatus;
import com.kallista.core.geojson.models.ValidationError;
import com.kallista.core.geojson.models.ValidationResult;
import com.kallista.core.geojson.services.CsvValidationService;
import com.kallista.core.geojson.services.GeoJsonAssetService;
import com.kallista.core.geojson.services.GeoJsonGenerationService;
import com.kallista.core.geojson.services.NotificationService;
import com.kallista.core.geojson.services.PublishService;
import com.kallista.core.geojson.services.StatusTrackingService;

@Designate(ocd = GeoJsonAssetServiceConfig.class)
@Component(service = GeoJsonProcessingServiceImpl.class)
public class GeoJsonProcessingServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(GeoJsonProcessingServiceImpl.class);
        private volatile String geoJsonPath = GeoJsonPipelineConstants.resolvePath(
            GeoJsonAssetServiceConfig.DEFAULT_DAM_BASE_PATH, GeoJsonPipelineConstants.GEOJSON_PATH);

    @Activate
    @Modified
    protected void activate(GeoJsonAssetServiceConfig config) {
        String basePath = config.damBasePath();
        if (basePath == null || basePath.trim().isEmpty()) {
            basePath = GeoJsonAssetServiceConfig.DEFAULT_DAM_BASE_PATH;
        }
        geoJsonPath = GeoJsonPipelineConstants.resolvePath(basePath, GeoJsonPipelineConstants.GEOJSON_PATH);
    }

    @Reference
    private CsvValidationService csvValidationService;

    @Reference
    private GeoJsonGenerationService geoJsonGenerationService;

    @Reference
    private GeoJsonAssetService geoJsonAssetService;

    @Reference
    private StatusTrackingService statusTrackingService;

    @Reference
    private PublishService publishService;

    @Reference
    private NotificationService notificationService;

    public void process(String assetPath, String uploader, String correlationId) throws IOException {
        statusTrackingService.updateStatus(assetPath, ProcessingStatus.VALIDATING, correlationId, null, null);

        ValidationResult validationResult;
        try (InputStream validationStream = geoJsonAssetService.openOriginalBinary(assetPath)) {
            validationResult = csvValidationService.validate(validationStream);
        }

        if (!validationResult.isValid()) {
            String invalidRowsSummary = validationResult.getErrors().stream()
                    .map(ValidationError::toString)
                    .collect(Collectors.joining("\n"));
            validationResult.getErrors().forEach(error -> LOG.warn(
                    "geojson validation error. correlationId={} assetPath={} rowNumber={}",
                    correlationId, assetPath, error.getRowNumber()));
            statusTrackingService.updateStatus(assetPath, ProcessingStatus.VALIDATION_FAILED, correlationId,
                    "CSV validation failed", invalidRowsSummary);
            notificationService.notifyValidationFailure(uploader, assetPath, invalidRowsSummary, correlationId);
            return;
        }

        statusTrackingService.updateStatus(assetPath, ProcessingStatus.GENERATING_GEOJSON, correlationId, null, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (InputStream generationStream = geoJsonAssetService.openOriginalBinary(assetPath)) {
            geoJsonGenerationService.generate(generationStream, outputStream);
        }

        statusTrackingService.updateStatus(assetPath, ProcessingStatus.SAVING, correlationId, null, null);
        geoJsonAssetService.backupExistingGeoJson();
        geoJsonAssetService.saveGeoJson(new ByteArrayInputStream(outputStream.toByteArray()));
        geoJsonAssetService.archiveCsvAsset(assetPath);

        statusTrackingService.updateStatus(assetPath, ProcessingStatus.PUBLISHING, correlationId, null, null);
        publishService.publishGeoJson();

        statusTrackingService.updateStatus(assetPath, ProcessingStatus.COMPLETED, correlationId, null, null);
        statusTrackingService.markPublished(assetPath, geoJsonPath);
        notificationService.notifySuccess(uploader, assetPath, geoJsonPath, correlationId);
        LOG.info("geojson processing completed. uploader={} assetPath={} correlationId={} bytes={}", uploader,
                assetPath, correlationId, outputStream.toString(StandardCharsets.UTF_8).length());
    }
}