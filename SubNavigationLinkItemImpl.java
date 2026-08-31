package com.kallista.core.models.impl;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.SubNavigationLinkItem;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       adapters = SubNavigationLinkItem.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class SubNavigationLinkItemImpl implements SubNavigationLinkItem {
    
    private static final Logger LOG = LoggerFactory.getLogger(SubNavigationLinkItemImpl.class);
    
    @ValueMapValue
    private String linkLabel;
    
    @ValueMapValue
    private String Link;

    @ValueMapValue
    private String classLabel;

    @ValueMapValue
    private String idLabel;

    @PostConstruct
    private void init() {
        LOG.info("Initializing SubNavigationLinkItemImpl");
        LOG.info("linkLabel: {}", linkLabel);
        LOG.info("Link: {}", Link);
    }
    
    @JsonProperty("linkLabel")
    @Override
    public String getLinkLabel() {
        return linkLabel;
    }
    
    @JsonProperty("Link")
    @Override
    public String getLink() {
        return Link;
    }

    @JsonProperty("classLabel")
    @Override
    public String getClassLabel() {
        return classLabel;
    }

    @JsonProperty("idLabel")
    @Override
    public String getIdLabel() {
        return idLabel;
    }
    
}