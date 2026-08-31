package com.kallista.core.geojson.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationResult {

    private final List<ValidationError> errors = new ArrayList<>();
    private int validRowCount;

    public void addError(ValidationError error) {
        errors.add(error);
    }

    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public int getValidRowCount() {
        return validRowCount;
    }

    public void incrementValidRowCount() {
        validRowCount++;
    }
}