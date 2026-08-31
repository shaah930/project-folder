/*
 *  Copyright 2025 Kohler Co.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package dsm.foundation.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

/**
 * Sling Model for the Spacer component
 * Provides configurable spacing with size options
 */
@Model(
    adaptables = {SlingHttpServletRequest.class, Resource.class},
    adapters = {SpacerModel.class, ComponentExporter.class},
    resourceType = SpacerModel.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = "json")
public class SpacerModel implements ComponentExporter {

    /**
     * The resource type that this model handles.
     */
    protected static final String RESOURCE_TYPE = "aem-dsm-foundation/components/spacer/v1/spacer";

    @SlingObject
    private Resource resource;

    @ValueMapValue
    private String size;

    /**
     * Gets the size value for the spacer
     * @return the size (none, xs, s, m, l, xl)
     */
    @JsonProperty("size")
    public String getSize() {
        return size;
    }

    /**
     * Checks if the spacer has a valid size configured
     * @return true if size is configured and not "none"
     */
    public boolean hasSize() {
        return size != null && !size.trim().isEmpty() && !"none".equals(size);
    }

    @Override
    public String getExportedType() {
        return resource != null ? resource.getResourceType() : "";
    }
}
