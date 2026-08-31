package com.kallista.core.models;

import com.kallista.core.constants.KallistaConstants;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { KallistaXFModel.class,
        ComponentExporter.class }, resourceType = KallistaConstants.RESOURCE_TYPE_KALLISTAXF, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class KallistaXFModel implements KallistaXF, ComponentExporter {

    @ValueMapValue
    private String XfPath;

    public String getXfPath() {
        return XfPath;
    }

    public String getExperienceFragmentsRoot() {
        return KallistaConstants.XF_ROOT_PATH;
    }

    @Override
    public String getExportedType() {
        return KallistaConstants.RESOURCE_TYPE_KALLISTAXF;
    }
}
