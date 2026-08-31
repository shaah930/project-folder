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
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.SellSheetCardItem;
import com.kallista.core.models.SellSheetCardsModel;

@Model(
    adaptables = {SlingHttpServletRequest.class, Resource.class},
    adapters = {SellSheetCardsModel.class, ComponentExporter.class},
    resourceType = SellSheetCardsModelImpl.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class SellSheetCardsModelImpl implements SellSheetCardsModel {

    private static final Logger LOG =
            LoggerFactory.getLogger(SellSheetCardsModelImpl.class);

    static final String RESOURCE_TYPE = "kallista/components/sellSheetCards";

    @SlingObject
    private Resource resource;

    @ValueMapValue(name = "title")
    private String title;

    @ValueMapValue(name = "label")
    private String label;

    @ValueMapValue(name = "link")
    private String link;

    @ChildResource(name = "sellSheetCards")
    private List<SellSheetCardItem> sellSheetCards;

    @PostConstruct
    private void init() {
        LOG.debug("Initializing SellSheetCardsModelImpl");
        LOG.debug("title: {}", title);
        LOG.debug("label: {}", label);
        LOG.debug("link: {}", link);
        LOG.debug("sellSheetCards nested multifield size: {}",
                sellSheetCards != null ? sellSheetCards.size() : "null");
    }

    @JsonProperty("title")
    @Override
    public String getTitle() {
        return title;
    }

    @JsonProperty("label")
    @Override
    public String getLabel() {
        return label;
    }

    @JsonProperty("link")
    @Override
    public String getLink() {
        return link;
    }

    @JsonProperty("sellSheetCards")
    @Override
    public List<SellSheetCardItem> getSellSheetCards() {
        return sellSheetCards != null
                ? sellSheetCards
                : Collections.emptyList();
    }

    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }
}