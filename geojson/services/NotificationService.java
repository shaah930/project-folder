package com.kallista.core.geojson.services;

public interface NotificationService {

    void notifyValidationFailure(String uploader, String assetPath, String invalidRowsSummary, String correlationId);

    void notifySuccess(String uploader, String assetPath, String outputPath, String correlationId);

    void notifyFailure(String uploader, String assetPath, String message, String correlationId);
}