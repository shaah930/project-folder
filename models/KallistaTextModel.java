package com.kallista.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.form.Text;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import lombok.experimental.Delegate;

@Model(
        adaptables = {Resource.class, SlingHttpServletRequest.class},
        adapters = {ComponentExporter.class, KallistaTextModel.class},
        resourceType = "kallista/components/form/text",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
	    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
	    extensions = ExporterConstants.SLING_MODEL_EXTENSION
	)
public class KallistaTextModel implements Text,ComponentExporter {
	
	static final String RESOURCE_TYPE= "kallista/components/form/text";

	@Self
	@Via(type=ResourceSuperType.class)
    @Delegate(types=Text.class)
    private Text parentTextModel;

    @ValueMapValue
    @JsonProperty("isDynamic")
    private boolean isDynamic;



    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }
}