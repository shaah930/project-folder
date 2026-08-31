package com.kallista.core.models.impl;

import com.kallista.core.models.ColumnDetailsItem;
import com.kallista.core.models.ImageDetailsItem;
import com.kallista.core.models.TitleItem;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import com.adobe.cq.export.json.ExporterConstants;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.Exporter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.sling.models.annotations.Via;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       adapters = TitleItem.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, 
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TitleItemImpl implements TitleItem {
    
    private static final Logger LOG = LoggerFactory.getLogger(TitleItemImpl.class);

    @ValueMapValue(name = "tabName")
    private String tabName;

    @ChildResource(name = "columnDetails")
    private List<ColumnDetailsItem> columnDetails;

    @ChildResource(name = "imageDetails")
    private List<ImageDetailsItem> imageDetails;

    @ValueMapValue(name = "ctaLabel")
    private String ctaLabel;

    @ValueMapValue(name = "ctaLink")
    private String ctaLink;

    @PostConstruct
    private void init() {
        LOG.debug("Initializing TitleItemImpl");
        LOG.debug("tabName: {}", tabName);
        LOG.debug("columnDetails size: {}", columnDetails != null ? columnDetails.size() : "null");
        LOG.debug("imageDetails size: {}", imageDetails != null ? imageDetails.size() : "null");
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
    public List<ColumnDetailsItem> getColumnDetails() {
        return columnDetails != null ? columnDetails : Collections.emptyList();
    }

    @JsonProperty("imageDetails")
    @Override
    public List<ImageDetailsItem> getImageDetails() {
        return imageDetails != null ? imageDetails : Collections.emptyList();
    }

}