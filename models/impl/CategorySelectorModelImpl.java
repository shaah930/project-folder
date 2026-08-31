package com.kallista.core.models.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.CategorySelectorItem;
import com.kallista.core.models.CategorySelectorModel;

@Model(adaptables = { SlingHttpServletRequest.class, Resource.class },
       adapters = { CategorySelectorModel.class, ComponentExporter.class },
       resourceType = CategorySelectorModelImpl.RESOURCE_TYPE,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class CategorySelectorModelImpl implements CategorySelectorModel {

    private static final Logger LOG = LoggerFactory.getLogger(CategorySelectorModelImpl.class);
    static final String RESOURCE_TYPE = "kallista/components/categorySelector";

    @ValueMapValue(name = "categoryName")
    private String categoryName;
    @ValueMapValue(name = "attributeName")
    private String attributeName;

    @ChildResource(name = "cards")
    private List<CategorySelectorItem> cards;

    @PostConstruct
    private void init() {
        LOG.debug("Initializing CategorySelectorModelImpl");
        LOG.debug("cards size: {}", cards != null ? cards.size() : "null");
    }

    @JsonProperty("categoryName")
    @Override
    public String getCategoryName() {
        return categoryName;
    }

    @JsonProperty("attributeName")
    @Override
    public String getAttributeName() {
        return attributeName;
    }

    @JsonProperty("cards")
    @Override
    public List<CategorySelectorItem> getCards() {
        return cards != null ? cards : Collections.emptyList();
    }

    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }
}
