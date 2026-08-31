package com.kallista.core.models.impl;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.ProductlistSortByItem;

@Model(
    adaptables = Resource.class,
    adapters = ProductlistSortByItem.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ProductlistSortByItemImpl implements ProductlistSortByItem {

    @ValueMapValue
    private String sortByLabel;

    @ValueMapValue
    private String lwFieldValue;

    @Override
    @JsonProperty("sortByLabel")
    public String getSortByLabel() {
        return sortByLabel;
    }

    @Override
    @JsonProperty("lwFieldValue")
    public String getLwFieldValue() {
        return lwFieldValue;
    }
}
