package com.kallista.core.models;

import java.util.List;

import com.adobe.cq.export.json.ComponentExporter;

public interface CategorySelectorModel extends ComponentExporter {
    String getCategoryName();

    String getAttributeName();
    List<CategorySelectorItem> getCards();
}
