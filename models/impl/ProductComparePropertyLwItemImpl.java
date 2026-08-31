package com.kallista.core.models.impl;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.ProductComparePropertyLwItem;

/**
 * Sling Model implementation for one Property Name / LW Key pair stored under
 * the {@code propertyLwItems} composite multifield of the productCompare
 * component.
 */
@Model(
    adaptables = Resource.class,
    adapters = ProductComparePropertyLwItem.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ProductComparePropertyLwItemImpl implements ProductComparePropertyLwItem {

    
    @ValueMapValue
    private String propertyName;

    @ValueMapValue
    private String lwKey;

    @Override
    @JsonProperty("propertyName")
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    @JsonProperty("lwKey")
    public String getLwKey() {
        return lwKey;
    }

}
