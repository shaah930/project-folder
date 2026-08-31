package com.kallista.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.kallista.core.constants.KallistaConstants;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { KallistaDynamicPDPModel.class,
        ComponentExporter.class }, resourceType = KallistaConstants.RESOURCE_TYPE_KALLISTA_DYNAMIC_PDP, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class KallistaDynamicPDPModel implements KallistaDynamicPDPItem, ComponentExporter {

    @ValueMapValue
    private String categoryXfPath;

    @ValueMapValue
    private String collectionXfPath;

    public String getCategoryXfPath() {
        return categoryXfPath;
    }

    public String getCollectionXfPath() {
        return collectionXfPath;
    }

    public String getExperienceFragmentsRoot() {
        return KallistaConstants.XF_ROOT_PATH;
    }

    @Override
    public String getExportedType() {
        return KallistaConstants.RESOURCE_TYPE_KALLISTA_DYNAMIC_PDP;
    }
}
