package com.kallista.core.models.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.GalleryCardModel;
import com.kallista.core.models.GalleryCardTab;

@Model(
    adaptables = {SlingHttpServletRequest.class, Resource.class},
    adapters = {GalleryCardModel.class, ComponentExporter.class},
    resourceType = GalleryCardModelImpl.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class GalleryCardModelImpl implements GalleryCardModel {

    private static final Logger LOG = LoggerFactory.getLogger(GalleryCardModelImpl.class);

    static final String RESOURCE_TYPE = "kallista/components/galleryCard";

    @SlingObject
    private Resource resource;

    @ChildResource(name = "tabs")
    private List<GalleryCardTab> tabs;


    @PostConstruct
    private void init() {
        LOG.debug("Initializing GalleryCardModelImpl");
        LOG.debug("tabs nested multifield size: {}",
                tabs != null ? tabs.size() : "null");
    }


    @JsonProperty("tabs")
    @Override
    public List<GalleryCardTab> getTabs() {
        return tabs != null
                ? tabs
                : Collections.emptyList();
    }


    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }
}
