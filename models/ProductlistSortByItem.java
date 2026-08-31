package com.kallista.core.models;

/**
 * Sling Model interface for one Sort By entry authored in the productlist
 * component's composite multifield.
 */
public interface ProductlistSortByItem {

    String getSortByLabel();

    String getLwFieldValue();
}
