package com.kallista.core.models;

import com.adobe.cq.export.json.ComponentExporter;

/**
 * Sling Model interface for the collectionData component.
 */
public interface CollectionDataModel extends ComponentExporter {

    String getCategoryName();

    String getAttributeName();

    String getAttributeValue();
}
