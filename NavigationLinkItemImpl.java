package com.kallista.core.models.impl;

import com.kallista.core.models.NavigationLinkItem;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import com.adobe.cq.export.json.ExporterConstants;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
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
       adapters = NavigationLinkItem.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NavigationLinkItemImpl implements NavigationLinkItem {
    
    private static final Logger LOG = LoggerFactory.getLogger(NavigationLinkItemImpl.class);


    
    @ValueMapValue(name = "ctaLabel")
    private String ctaLabel;
        
    @ValueMapValue(name = "ctaLink")
    private String ctaLink;


    @PostConstruct
    private void init() {
        LOG.debug("Initializing NavigationLinkItemImpl");

        
        LOG.debug("ctaLabel: {}", ctaLabel);
        

        
        LOG.debug("ctaLink: {}", ctaLink);
        

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
    


}