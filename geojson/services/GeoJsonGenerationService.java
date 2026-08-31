package com.kallista.core.geojson.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.day.cq.dam.api.Asset;
import com.kallista.core.geojson.models.GeneratedGeoJsonResult;

public interface GeoJsonGenerationService {

    void generate(InputStream csvInputStream, OutputStream outputStream) throws IOException;

    GeneratedGeoJsonResult generate(Asset asset) throws IOException;
}