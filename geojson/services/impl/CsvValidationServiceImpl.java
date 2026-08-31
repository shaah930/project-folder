package com.kallista.core.geojson.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.osgi.service.component.annotations.Component;

import com.day.cq.dam.api.Asset;
import com.kallista.core.geojson.models.ValidationError;
import com.kallista.core.geojson.models.ValidationResult;
import com.kallista.core.geojson.services.CsvValidationService;

@Component(service = CsvValidationService.class)
public class CsvValidationServiceImpl implements CsvValidationService {

    private static final String ENTITY_ID = "EntityID";
    private static final String LOCATION_NAME = "LocationName";
    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";

    @Override
    public ValidationResult validate(Asset asset) throws IOException {
        return validate(asset.getOriginal().getStream());
    }

    @Override
    public ValidationResult validate(InputStream inputStream) throws IOException {
        ValidationResult result = new ValidationResult();
        Set<String> seenEntityIds = new HashSet<>();

        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setIgnoreSurroundingSpaces(true)
                        .build()
                        .parse(reader)) {
            for (CSVRecord record : parser) {
                int rowNumber = (int) record.getRecordNumber() + 1;
                String entityId = trim(record.get(ENTITY_ID));
                boolean rowValid = true;

                if (isBlank(entityId)) {
                    result.addError(new ValidationError(rowNumber, entityId, ENTITY_ID, "EntityID is mandatory"));
                    rowValid = false;
                } else if (!seenEntityIds.add(entityId)) {
                    result.addError(new ValidationError(rowNumber, entityId, ENTITY_ID, "EntityID must be unique"));
                    rowValid = false;
                }

                String locationName = trim(record.get(LOCATION_NAME));
                if (isBlank(locationName)) {
                    result.addError(new ValidationError(rowNumber, entityId, LOCATION_NAME,
                            "LocationName must be present and non-empty"));
                    rowValid = false;
                }

                rowValid &= validateCoordinate(result, rowNumber, entityId, LATITUDE, record.get(LATITUDE), -90D, 90D);
                rowValid &= validateCoordinate(result, rowNumber, entityId, LONGITUDE, record.get(LONGITUDE), -180D,
                        180D);

                if (rowValid) {
                    result.incrementValidRowCount();
                }
            }
        }
        return result;
    }

    private static boolean validateCoordinate(ValidationResult result, int rowNumber, String entityId, String field,
            String value, double min, double max) {
        String trimmed = trim(value);
        if (isBlank(trimmed)) {
            result.addError(new ValidationError(rowNumber, entityId, field, field + " is mandatory"));
            return false;
        }
        try {
            double parsed = Double.parseDouble(trimmed);
            if (parsed < min || parsed > max) {
                result.addError(new ValidationError(rowNumber, entityId, field,
                        field + " must be between " + min + " and " + max));
                return false;
            }
            return true;
        } catch (NumberFormatException ex) {
            result.addError(new ValidationError(rowNumber, entityId, field, field + " must be numeric"));
            return false;
        }
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}