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
import com.kallista.core.models.GalleryCard;
import com.kallista.core.models.GalleryCardTab;

@Model(
    adaptables = {Resource.class, SlingHttpServletRequest.class},
    adapters = GalleryCardTab.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class GalleryCardTabImpl implements GalleryCardTab {

    private static final Logger LOG = LoggerFactory.getLogger(GalleryCardTabImpl.class);

    @ValueMapValue(name = "tabTitle")
    private String tabTitle;

    @ChildResource(name = "cards")
    private List<GalleryCard> cards;


    @PostConstruct
    protected void init() {
        LOG.debug("Initializing GalleryCardTabImpl");
        LOG.debug("tabTitle: {}", tabTitle);
        LOG.debug("cards nested multifield size: {}",
                cards != null ? cards.size() : "null");
    }


    @JsonProperty("tabTitle")
    @Override
    public String getTabTitle() {
        return tabTitle;
    }


    @JsonProperty("cards")
    @Override
    public List<GalleryCard> getCards() {
        return cards != null
                ? cards
                : Collections.emptyList();
    }
}
