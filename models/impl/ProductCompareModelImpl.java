package com.kallista.core.models.impl;

import java.util.Collections;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.ProductComparePropertyLwItem;
import com.kallista.core.models.ProductCompareModel;

/**
 * Sling Model implementation for {@code kallista/components/productCompare}.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = { ProductCompareModel.class, ComponentExporter.class },
    resourceType = ProductCompareModelImpl.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ProductCompareModelImpl implements ProductCompareModel {

    static final String RESOURCE_TYPE = "kallista/components/productCompare";

    @ValueMapValue
    private String selectionTitle;

    @ValueMapValue
    private String helperText;

    @ValueMapValue
    private String continueShoppingCtaLink;

    @ValueMapValue
    private String continueShoppingCtaLabel;

    @ValueMapValue
    private String addProductCtaLink;

    @ValueMapValue
    private String addProductCtaLabel;

    @ValueMapValue
    private String viewMoreDetailsCtaLabel;

    @ValueMapValue
    private String viewMoreDetailsCtaLink;

    @ValueMapValue
    private String subCategoryName;

    @ChildResource
    private List<ProductComparePropertyLwItem> propertyLwItems;

    @Override
    @JsonProperty("selectionTitle")
    public String getSelectionTitle() {
        return selectionTitle;
    }

    @Override
    @JsonProperty("helperText")
    public String getHelperText() {
        return helperText;
    }

    @Override
    @JsonProperty("continueShoppingCtaLabel")
    public String getContinueShoppingCtaLabel() {
        return continueShoppingCtaLabel;
    }

    @Override
    @JsonProperty("continueShoppingCtaLink")
    public String getContinueShoppingCtaLink() {
        return continueShoppingCtaLink;
    }

    @Override
    @JsonProperty("addProductCtaLabel")
    public String getAddProductCtaLabel() {
        return addProductCtaLabel;
    }

    @Override
    @JsonProperty("addProductCtaLink")
    public String getAddProductCtaLink() {
        return addProductCtaLink;
    }

    @Override
    @JsonProperty("viewMoreDetailsCtaLabel")
    public String getViewMoreDetailsCtaLabel() {
        return viewMoreDetailsCtaLabel;
    }

    @Override
    @JsonProperty("viewMoreDetailsCtaLink")
    public String getViewMoreDetailsCtaLink() {
        return viewMoreDetailsCtaLink;
    }

    @Override
    @JsonProperty("subCategoryName")
    public String getSubCategoryName() {
        return subCategoryName;
    }
    
    @Override
    @JsonProperty("propertyLwItems")
    public List<ProductComparePropertyLwItem> getPropertyLwItems() {
        return propertyLwItems != null ? propertyLwItems : Collections.emptyList();
    }
    
    @Override   
    public String getExportedType() {
    return RESOURCE_TYPE;
    }
}
