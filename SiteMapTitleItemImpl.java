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
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.ColumnDetailsItem;
import com.kallista.core.models.SiteMapColumnDetailsItem;
import com.kallista.core.models.SiteMapTitleItem;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       adapters = SiteMapTitleItem.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, 
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class SiteMapTitleItemImpl implements SiteMapTitleItem {
    
    private static final Logger LOG = LoggerFactory.getLogger(SiteMapTitleItemImpl.class);

    @ValueMapValue(name = "tabName")
    private String tabName;

    @ChildResource(name = "columnDetails")
    private List<SiteMapColumnDetailsItem> columnDetails;

    @ValueMapValue(name = "ctaLabel")
    private String ctaLabel;

    @ValueMapValue(name = "ctaLink")
    private String ctaLink;

    @PostConstruct
    private void init() {
        LOG.debug("Initializing TitleItemImpl");
        LOG.debug("tabName: {}", tabName);
        LOG.debug("columnDetails size: {}", columnDetails != null ? columnDetails.size() : "null");
        LOG.debug("ctaLabel: {}", ctaLabel);
        LOG.debug("ctaLink: {}", ctaLink);
    }

    @JsonProperty("tabName")
    @Override
    public String getTabName() {
        return tabName;
    }

    @JsonProperty("columnDetails")
    @Override
    public List<SiteMapColumnDetailsItem> getColumnDetails() {
        return columnDetails != null ? columnDetails : Collections.emptyList();
    }
}