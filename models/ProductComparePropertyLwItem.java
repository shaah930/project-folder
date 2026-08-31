package com.kallista.core.models;

/**
 * Sling Model interface for a single Property Name / LW Key pair authored in
 * the productCompare component's composite multifield.
 */
public interface ProductComparePropertyLwItem {

    String getPropertyName();

    String getLwKey();
}
