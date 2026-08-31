package com.kallista.core.models.impl;

import com.kallista.core.models.CtaDetailsItem;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import com.adobe.cq.export.json.ExporterConstants;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.Exporter;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       adapters = CtaDetailsItem.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, 
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class CtaDetailsItemImpl implements CtaDetailsItem {
    
    private static final Logger LOG = LoggerFactory.getLogger(CtaDetailsItemImpl.class);

    @ValueMapValue(name = "ctaLabel")
    private String ctaLabel;
    
    @ValueMapValue(name = "ctaLink")
    private String ctaLink;
    
    @ValueMapValue(name = "ctaType")
    private String ctaType;

    @ValueMapValue(name = "linkNewTab")
    private boolean linkNewTab;
   
    @PostConstruct
    private void init() {
        LOG.debug("Initializing CtaDetailsItemImpl");
        LOG.debug("ctaLabel: {}", ctaLabel);
        LOG.debug("ctaLink: {}", ctaLink);
        LOG.debug("ctaType: {}", ctaType);
        LOG.debug("linkNewTab: {}", linkNewTab);
    }
    
    @JsonProperty("ctaLabel")
    @Override
    public String getCtaLabel() {
        return ctaLabel;
    }
    
    @JsonProperty("ctaLink")
    @Override
    public String getCtaLink() {
        return ctaLink;
    }

    @JsonProperty("ctaType")
    @Override
    public String getCtaType() {
        return ctaType;
    }

    @JsonProperty("linkNewTab")
    @Override
    public boolean isLinkNewTab() {
        return linkNewTab;
    }
    
}