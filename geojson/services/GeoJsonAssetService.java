package com.kallista.core.geojson.services;

import java.io.IOException;
import java.io.InputStream;

import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.dam.api.Asset;

public interface GeoJsonAssetService {

    InputStream openOriginalBinary(String assetPath) throws IOException;

    void backupExistingGeoJson();

    void saveGeoJson(InputStream inputStream) throws IOException;

    void archiveCsvAsset(String assetPath);

    String saveGeoJson(ResourceResolver resolver, java.nio.file.Path geoJsonFile) throws IOException;

    String archiveCsv(ResourceResolver resolver, Asset csvAsset);
}