package com.kallista.core.models.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.FinishesDataModel;
import com.kallista.core.models.FinishesTabItem;

@Model(
    adaptables = {SlingHttpServletRequest.class, Resource.class},
    adapters = {FinishesDataModel.class, ComponentExporter.class},
    resourceType = FinishesDataModelImpl.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class FinishesDataModelImpl implements FinishesDataModel {

    private static final Logger LOG = LoggerFactory.getLogger(FinishesDataModelImpl.class);
    static final String RESOURCE_TYPE = "kallista/components/finishesData";

    @ValueMapValue
	private String title;

	@ValueMapValue
	private String descriptionText;

    @ChildResource(name = "finishesTab")
    private List<FinishesTabItem> finishesTabItems;

    @PostConstruct
    private void init() {
        LOG.debug("Initializing FinishesDataModelImpl");
        LOG.debug("title: {}", title);
        LOG.debug("descriptionText: {}", descriptionText);
        LOG.debug("FinishesTabItems multifield size: {}",
                finishesTabItems != null ? finishesTabItems.size() : "null");
    }

    @JsonProperty("title")
    @Override
    public String getTitle() {
        return title;
    }

    @JsonProperty("descriptionText")
    @Override
    public String getDescriptionText() {
        return descriptionText;
    }

    @JsonProperty("finishesTabItems")
    @Override
    public List<FinishesTabItem> getFinishesTabItems() {
        return finishesTabItems != null
                ? finishesTabItems
                : Collections.emptyList();
    }

    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }
}
