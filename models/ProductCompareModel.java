package com.kallista.core.models;

import java.util.List;
import com.adobe.cq.export.json.ComponentExporter;


/**
 * Sling Model interface for the productCompare component.
 */
public interface ProductCompareModel extends ComponentExporter {

    String getSelectionTitle();

    String getHelperText();

    String getContinueShoppingCtaLabel();

    String getContinueShoppingCtaLink();

    String getAddProductCtaLabel();

    String getAddProductCtaLink();

    String getViewMoreDetailsCtaLabel();

    String getViewMoreDetailsCtaLink();

    String getSubCategoryName();

    List<ProductComparePropertyLwItem> getPropertyLwItems();
}
