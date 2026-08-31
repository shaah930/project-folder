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
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.SiteMapModel;
import com.kallista.core.models.SiteMapTitleItem;



@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
        adapters = { SiteMapModel.class,ComponentExporter.class},
        resourceType = SiteMapModelImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class SiteMapModelImpl implements SiteMapModel {

    private static final Logger LOG = LoggerFactory.getLogger(SiteMapModelImpl.class);
    static final String RESOURCE_TYPE = "kallista/components/sitemap";

    @SlingObject
    private Resource resource;

    @Inject
    @Via("resource")
    private List<SiteMapTitleItem> title;

    @PostConstruct
    private void init() {
        LOG.debug("Initializing SiteMapModelImpl");

        LOG.debug("title nested multifield size: {}", title != null ? title.size() : "null");

        LOG.debug("title nested multifield size: {}", title != null ? title.size() : "null");
    }

    @JsonProperty("title")
    @Override
    public List<SiteMapTitleItem> getTitle() {
        return title != null ? title : Collections.emptyList();
    }


    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }

}