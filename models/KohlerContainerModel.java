package dsm.foundation.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.LayoutContainer;
import com.day.cq.wcm.foundation.model.export.AllowedComponentsExporter;
import com.day.cq.wcm.foundation.model.responsivegrid.ResponsiveGrid;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;

/**
 * Custom Container Model for KHS
 * Only used for JSON export (.model.json) to exclude allowedComponents from JSON payload.
 * HTML rendering uses the default core container model.
 * 
 * This model ONLY registers as ComponentExporter (for JSON), not as LayoutContainer (for HTL).
 * This ensures HTML rendering is not affected.
 */
@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = ComponentExporter.class,  // Only register for JSON export, not HTL rendering
    resourceType = "aem-dsm-foundation/components/container",
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
@JsonIgnoreProperties({"allowedComponents", "exportedAllowedComponents"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KohlerContainerModel implements ComponentExporter, LayoutContainer {

    private static final Logger LOG = LoggerFactory.getLogger(KohlerContainerModel.class);
    
    protected static final String RESOURCE_TYPE = "aem-dsm-foundation/components/container";

    @SlingObject
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    @ValueMapValue
    @Via("resource")
    public String dsmModule;

    @ValueMapValue(name = "data-smartcrop-disabled")
    @Via("resource")
    private Boolean dataSmartCropDisabled;

    @Self
    @Via(type = ResourceSuperType.class)
    private LayoutContainer layoutContainer;

    @Self
    private ResponsiveGrid responsiveGrid;
    
    private boolean isJsonRequest = false;
    
    @PostConstruct
    protected void init() {
        // Check if this is a JSON export request
        if (request != null && request.getRequestPathInfo() != null) {
            String extension = request.getRequestPathInfo().getExtension();
            String selector = request.getRequestPathInfo().getSelectorString();
            isJsonRequest = "json".equals(extension) || 
                           (selector != null && selector.contains("model"));
            
            LOG.info("===================  KHS ContainerModel Init ==================");
            LOG.info("Request Extension: {}", extension);
            LOG.info("Request Selector: {}", selector);
            LOG.info("Is JSON Request: {}", isJsonRequest);
            LOG.info("Resource path: {}", resource != null ? resource.getPath() : "NULL");
            LOG.info("Resource type: {}", resource != null ? resource.getResourceType() : "NULL");
            LOG.info("LayoutContainer injected: {}", layoutContainer != null);
            LOG.info("ResponsiveGrid injected: {}", responsiveGrid != null);
            LOG.info("==============================================================");
        }
    }

    @Override
    public String[] getExportedItemsOrder() {
        return layoutContainer != null ? layoutContainer.getExportedItemsOrder() : new String[0];
    }

    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return layoutContainer != null ? layoutContainer.getExportedItems() : java.util.Collections.emptyMap();
    }

    @Override
    public String getExportedType() {
        return resource != null ? resource.getResourceType() : RESOURCE_TYPE;
    }

    @Override
    public String getBackgroundStyle() {
        return layoutContainer != null ? layoutContainer.getBackgroundStyle() : null;
    }

    @Override
    public String getId() {
        return layoutContainer != null ? layoutContainer.getId() : null;
    }

    public String getDsmModule() {
        return dsmModule != null ? dsmModule : "none";
    }

    @JsonProperty("data-smartcrop-disabled")
    public Boolean getDataSmartCropDisabled() {
        return dataSmartCropDisabled;
    }

    // Delegate ResponsiveGrid methods
    public int getColumnCount() {
        return responsiveGrid != null ? responsiveGrid.getColumnCount() : 12;
    }

    public Map<String, String> getColumnClassNames() {
        return responsiveGrid != null ? responsiveGrid.getColumnClassNames() : Collections.emptyMap();
    }

    public String getGridClassNames() {
        return responsiveGrid != null ? responsiveGrid.getGridClassNames() : "aem-Grid aem-Grid--12";
    }

    /**
     * Override exportedAllowedComponents to return null
     * This prevents the exportedAllowedComponents object from being included in the JSON export,
     * significantly reducing payload size.
     *
     * @return null (excluded from JSON)
     */
    @JsonIgnore
    public AllowedComponentsExporter getExportedAllowedComponents() {
        return null;
    }

    /**
     * Override allowedComponents to return null
     * This prevents the allowedComponents object from being included in the JSON export,
     * significantly reducing payload size.
     *
     * @return null (excluded from JSON)
     */
    @JsonIgnore
    public AllowedComponentsExporter getAllowedComponents() {
        return null;
    }
}

