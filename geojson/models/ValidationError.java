package com.kallista.core.geojson.models;

public class ValidationError {

    private final int rowNumber;
   // private final String entityId;
    private final String field;
    //private final String reason;

    public ValidationError(int rowNumber, String entityId, String reason) {
        this(rowNumber, entityId, "general", reason);
    }

    public ValidationError(int rowNumber, String entityId, String field, String reason) {
        this.rowNumber = rowNumber;
        //this.entityId = entityId;
        this.field = field;
       // this.reason = reason;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    // public String getEntityId() {
    //     return entityId;
    // }

    public String getField() {
        return field;
    }

    // public String getReason() {
    //     return reason;
    // }

    @Override
    public String toString() {
        return "row=" + rowNumber + ", field=" + field; // + ", reason=" + reason;
    }
}