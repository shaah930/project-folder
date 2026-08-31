package com.kallista.core.models.impl;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.SubHeadingItem;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       adapters = SubHeadingItem.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class SubHeadingItemImpl implements SubHeadingItem {

    private static final Logger LOG = LoggerFactory.getLogger(SubHeadingItemImpl.class);

    @ValueMapValue(name = "subheadingLabel")
    private String subheadingLabel;

    @ValueMapValue(name = "subheadingLink")
    private String subheadingLink;

    @PostConstruct
    private void init() {
        LOG.debug("Initializing SubHeadingItemImpl");
        LOG.debug("subheadingLabel: {}", subheadingLabel);
        LOG.debug("subheadingLink: {}", subheadingLink);
    }

    @JsonProperty("subheadingLabel")
    @Override
    public String getSubheadingLabel() {
        return subheadingLabel;
    }

    @JsonProperty("subheadingLink")
    @Override
    public String getSubheadingLink() {
        return subheadingLink;
    }
}
