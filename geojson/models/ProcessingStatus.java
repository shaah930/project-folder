package com.kallista.core.geojson.models;

public enum ProcessingStatus {
    UPLOADED,
    VALIDATING,
    VALIDATION_FAILED,
    GENERATING_GEOJSON,
    SAVING,
    PUBLISHING,
    COMPLETED,
    FAILED
}