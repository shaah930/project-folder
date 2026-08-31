package com.kallista.core.geojson.services;

import java.io.IOException;
import java.io.InputStream;

import java.io.IOException;

import com.day.cq.dam.api.Asset;
import com.kallista.core.geojson.models.ValidationResult;

public interface CsvValidationService {

    ValidationResult validate(InputStream inputStream) throws IOException;

    ValidationResult validate(Asset asset) throws IOException;
}