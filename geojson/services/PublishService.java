package com.kallista.core.geojson.services;

import org.apache.sling.api.resource.ResourceResolver;

public interface PublishService {

    void publishGeoJson();

    void publish(ResourceResolver resolver, String assetPath);
}