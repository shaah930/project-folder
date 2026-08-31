package com.kallista.core.geojson.services;

public interface GeoJsonJobProducer {

    String newCorrelationId();

    boolean enqueue(String assetPath, String uploader, String correlationId, int retryCount, long delayMillis);
}
