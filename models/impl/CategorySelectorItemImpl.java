package com.kallista.core.models.impl;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.CategorySelectorItem;

import dsm.foundation.core.utils.AssetUtils;

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class },
       adapters = CategorySelectorItem.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class CategorySelectorItemImpl implements CategorySelectorItem {

    private static final Logger LOG = LoggerFactory.getLogger(CategorySelectorItemImpl.class);

    @ValueMapValue(name = "imageReference")
    private String imageReference;

    @ValueMapValue(name = "alt")
    private String alt;

    @ValueMapValue(name = "title")
    private String title;

    @ValueMapValue(name = "attributeValue")
    private String attributeValue;
    
    @Inject
    PublishUtils publishUtils;

    @SlingObject
    private Resource resource;

    @PostConstruct
    private void init() {
        LOG.debug("Initializing CategorySelectorItemImpl");
        LOG.debug("imageReference: {}", AssetUtils.getScene7AssetPath(imageReference, resource, publishUtils));
        LOG.debug("title: {}", title);
        LOG.debug("attributeValue: {}", attributeValue);
    }

    @JsonProperty("imageReference")
    @Override
    public String getImageReference() {
    	return  AssetUtils.getScene7AssetPath(imageReference, resource, publishUtils); 
    }

    
    @JsonProperty("alt")
    @Override
    public String getAlt() {
        return alt;
    }

    @JsonProperty("title")
    @Override
    public String getTitle() {
        return title;
    }

    @JsonProperty("attributeValue")
    @Override
    public String getAttributeValue() {
        return attributeValue;
    }
}
