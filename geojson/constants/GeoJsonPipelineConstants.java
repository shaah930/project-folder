package com.kallista.core.geojson.constants;

public final class GeoJsonPipelineConstants {

    private GeoJsonPipelineConstants() {
    }

    public static final String JOB_TOPIC = "kallista/geojson/generate";
    public static final String SUBSERVICE_NAME = "kohlersubservice";

    public static final String CSV_INCOMING_PATH = "/csv/incoming";
    public static final String CSV_ARCHIVE_PATH = "/csv/archive";
    public static final String GEOJSON_PATH = "/geoJson/stores.geojson";
    public static final String GEOJSON_ARCHIVE_PATH = "/geoJson/archive";

    public static String resolvePath(String basePath, String relativePath) {
        String normalizedBasePath = basePath == null || basePath.trim().isEmpty() ? "/" : basePath.trim();
        normalizedBasePath = normalizedBasePath.replaceAll("/+$", "");
        return normalizedBasePath + (relativePath.startsWith("/") ? relativePath : "/" + relativePath);
    }

    public static final String STATUS_PROP = "kallista:geojsonStatus";
    public static final String STATUS_UPDATED_AT_PROP = "kallista:geojsonStatusUpdatedAt";
    public static final String STATUS_ERROR_PROP = "kallista:geojsonErrorSummary";
    public static final String STATUS_INVALID_ROWS_PROP = "kallista:geojsonInvalidRows";
    public static final String CORRELATION_ID_PROP = "kallista:geojsonCorrelationId";
    public static final String NOTIFICATION_PROP = "kallista:geojsonNotificationMessage";
    public static final String OUTPUT_PATH_PROP = "kallista:geojsonOutputPath";
    public static final String PUBLISHED_AT_PROP = "kallista:geojsonPublishedAt";

    public static final String JOB_PROP_ASSET_PATH = "assetPath";
    public static final String JOB_PROP_UPLOADER = "uploader";
    public static final String JOB_PROP_CORRELATION_ID = "correlationId";
    public static final String JOB_PROP_RETRY_COUNT = "retryCount";
    public static final String JOB_PROP_SCHEDULED_AT = "scheduledAt";
}