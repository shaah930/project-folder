package com.kallista.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import com.adobe.cq.wcm.core.components.models.LayoutContainer;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dsm.foundation.core.models.KohlerContainerModel;
import lombok.experimental.Delegate;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = {ComponentExporter.class,KallistaContainerModel.class},
    resourceType = "kallista/components/container",
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
@JsonIgnoreProperties({"allowedComponents", "exportedAllowedComponents"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KallistaContainerModel extends KohlerContainerModel implements ComponentExporter {

    private static final Logger log = LoggerFactory.getLogger(KallistaContainerModel.class);

    protected static final String RESOURCE_TYPE = "kallista/components/container";


    @ValueMapValue(name = "data-toggle-off")
    @Via("resource")
    private Boolean dataToggleOff;

    @SlingObject
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    @Override
    public String getExportedType() {
        if (resource != null) {
            return resource.getResourceType();
        }
		/*
		 * if (kohlerContainerModel != null) { return
		 * kohlerContainerModel.getExportedType(); }
		 */
        return RESOURCE_TYPE;
    }

    @JsonProperty("data-toggle-off")
    public Boolean getDataToggleOff() {
        return dataToggleOff;
    }
}
