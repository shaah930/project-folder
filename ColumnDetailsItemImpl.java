package com.kallista.core.models.impl;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.ColumnDetailsItem;
import com.kallista.core.models.SubHeadingItem;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       adapters = ColumnDetailsItem.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ColumnDetailsItemImpl implements ColumnDetailsItem {

    @ValueMapValue(name = "heading")
    private String heading;

    @ChildResource(name = "subHeading")
    private List<SubHeadingItem> subHeading;

    @JsonProperty("heading")
    @Override
    public String getHeading() {
        return heading;
    }

    @JsonProperty("subHeading")
    @Override
    public List<SubHeadingItem> getSubHeading() {
        return subHeading != null ? subHeading : Collections.emptyList();
    }
}
