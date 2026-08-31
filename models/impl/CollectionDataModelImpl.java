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
import com.kallista.core.models.CollectionDataModel;

/**
 * Sling Model implementation for {@code kallista/components/collectionData}.
 */
@Model(
    adaptables = { SlingHttpServletRequest.class, Resource.class },
    adapters = { CollectionDataModel.class, ComponentExporter.class },
    resourceType = CollectionDataModelImpl.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class CollectionDataModelImpl implements CollectionDataModel {

    static final String RESOURCE_TYPE = "kallista/components/collectionData";

    @ValueMapValue
    private String categoryName;

    @ValueMapValue
    private String attributeName;

    @ValueMapValue
    private String attributeValue;

    @Override
    @JsonProperty("categoryName")
    public String getCategoryName() {
        return categoryName;
    }

    @Override
    @JsonProperty("attributeName")
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    @JsonProperty("attributeValue")
    public String getAttributeValue() {
        return attributeValue;
    }

    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }
}
