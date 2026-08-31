package com.kallista.core.geojson.services;

import com.kallista.core.geojson.models.ProcessingStatus;

public interface StatusTrackingService {

    void updateStatus(String assetPath, ProcessingStatus status, String correlationId, String errorSummary,
            String invalidRowsSummary);

    void markPublished(String assetPath, String outputPath);

    boolean isInFlight(String assetPath);

    boolean isCompleted(String assetPath);

    boolean tryMarkQueued(String assetPath, String correlationId);

    boolean isCurrentCorrelation(String assetPath, String correlationId);
}