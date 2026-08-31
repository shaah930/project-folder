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
import com.kallista.core.models.FinishesTabItem;

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class },
       adapters = FinishesTabItem.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class FinishesTabItemImpl implements FinishesTabItem {

    private static final Logger LOG = LoggerFactory.getLogger(FinishesTabItemImpl.class);

    @ValueMapValue(name = "tabLabel")
    private String tabLabel;

    @ValueMapValue(name = "lwValue")
    private String lwValue;

    @ValueMapValue(name = "categoryName")
    private String categoryName;

    @PostConstruct
    private void init() {
        LOG.debug("Initializing FinishesTabItemImpl");
        LOG.debug("tabLabel: {}", tabLabel);
        LOG.debug("lwValue: {}", lwValue);
        LOG.debug("categoryName: {}", categoryName);
    }

    @JsonProperty("tabLabel")
    @Override
    public String getTabLabel() {
        return tabLabel;
    }

    @JsonProperty("lwValue")
    @Override
    public String getLwValue() {
        return lwValue;
    }

    @JsonProperty("categoryName")
    @Override
    public String getCategoryName() {
        return categoryName;
    }


}
