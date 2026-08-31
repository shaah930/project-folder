package com.kallista.core.models.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.BenefitCardItem;
import com.kallista.core.models.BenefitCardsModel;

@Model(
    adaptables = {SlingHttpServletRequest.class, Resource.class},
    adapters = {BenefitCardsModel.class, ComponentExporter.class},
    resourceType = BenefitCardsModelImpl.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class BenefitCardsModelImpl implements BenefitCardsModel {

    private static final Logger LOG = LoggerFactory.getLogger(BenefitCardsModelImpl.class);

    static final String RESOURCE_TYPE = "kallista/components/benefitCards";

    @SlingObject
    private Resource resource;

    @ChildResource(name = "benefitCardItems")
    private List<BenefitCardItem> benefitCardItems;


    @PostConstruct
    private void init() {
        LOG.debug("Initializing BenefitCardsModelImpl");
        LOG.debug("benefitCardItems nested multifield size: {}",
                benefitCardItems != null ? benefitCardItems.size() : "null");
    }


    @JsonProperty("benefitCardItems")
    @Override
    public List<BenefitCardItem> getBenefitCardItems() {
        return benefitCardItems != null
                ? benefitCardItems
                : Collections.emptyList();
    }


    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }
}