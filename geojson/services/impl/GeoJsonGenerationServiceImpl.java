package com.kallista.core.geojson.services.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.osgi.service.component.annotations.Component;

import com.day.cq.dam.api.Asset;
import com.kallista.core.geojson.models.GeneratedGeoJsonResult;
import com.kallista.core.geojson.services.GeoJsonGenerationService;

@Component(service = GeoJsonGenerationService.class)
public class GeoJsonGenerationServiceImpl implements GeoJsonGenerationService {

    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";

    @Override
    public GeneratedGeoJsonResult generate(Asset asset) throws IOException {
        Path tempFile = Files.createTempFile("stores-geojson-", ".geojson");
        int featureCount;

        try (InputStream inputStream = asset.getOriginal().getStream();
                BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
            featureCount = writeFeatureCollection(inputStream, writer);
        }

        return new GeneratedGeoJsonResult(tempFile, featureCount);
    }

    @Override
    public void generate(InputStream csvInputStream, OutputStream outputStream) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            writeFeatureCollection(csvInputStream, writer);
            writer.flush();
        }
    }

    private static int writeFeatureCollection(InputStream csvInputStream, BufferedWriter writer) throws IOException {
        int featureCount = 0;
        try (Reader reader = new InputStreamReader(csvInputStream, StandardCharsets.UTF_8);
                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setIgnoreSurroundingSpaces(true)
                        .build()
                        .parse(reader)) {
            writer.write("{\"type\":\"FeatureCollection\",\"features\":[");
            boolean firstFeature = true;
            List<String> headers = parser.getHeaderNames();
            for (CSVRecord record : parser) {
                if (!firstFeature) {
                    writer.write(',');
                }
                writer.write(toFeature(record, headers));
                firstFeature = false;
                featureCount++;
            }
            writer.write("]}");
        }
        return featureCount;
    }

    private static String toFeature(CSVRecord record, List<String> headers) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[");
        builder.append(record.get(LONGITUDE).trim()).append(',').append(record.get(LATITUDE).trim()).append("]},");
        builder.append("\"properties\":{");
        boolean first = true;
        for (String header : headers) {
            if (!first) {
                builder.append(',');
            }
            String propertyName = "EntityID".equals(header) ? "id" : header;
            builder.append('"').append(escapeJson(propertyName)).append('"').append(':');
            builder.append('"').append(escapeJson(record.get(header))).append('"');
            first = false;
        }
        builder.append("}}");
        return builder.toString();
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder(value.length() + 8);
        for (char character : value.toCharArray()) {
            switch (character) {
                case '\\':
                    builder.append("\\\\");
                    break;
                case '"':
                    builder.append("\\\"");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                default:
                    builder.append(character);
            }
        }
        return builder.toString();
    }
}