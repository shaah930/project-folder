package com.kallista.core.models.impl;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.UtilityDetailsItem;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       adapters = UtilityDetailsItem.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class UtilityDetailsItemImpl implements UtilityDetailsItem {

    private static final Logger LOG = LoggerFactory.getLogger(UtilityDetailsItemImpl.class);

    @ValueMapValue(name = "utilityLabel")
    private String utilityLabel;

    @ValueMapValue(name = "utilityLink")
    private String utilityLink;

    @PostConstruct
    private void init() {
        LOG.debug("Initializing UtilityDetailsItemImpl");
        LOG.debug("utilityLabel: {}", utilityLabel);
        LOG.debug("utilityLink: {}", utilityLink);
    }

    @JsonProperty("utilityLabel")
    @Override
    public String getUtilityLabel() {
        return utilityLabel;
    }

    @JsonProperty("utilityLink")
    @Override
    public String getUtilityLink() {
        return utilityLink;
    }
}
