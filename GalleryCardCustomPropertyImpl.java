package com.kallista.core.models.impl;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.GalleryCardCustomProperty;

@Model(
    adaptables = {Resource.class, SlingHttpServletRequest.class},
    adapters = GalleryCardCustomProperty.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class GalleryCardCustomPropertyImpl implements GalleryCardCustomProperty {

    private static final Logger LOG = LoggerFactory.getLogger(GalleryCardCustomPropertyImpl.class);

    @ValueMapValue(name = "key")
    private String key;

    @ValueMapValue(name = "value")
    private String value;


    @PostConstruct
    protected void init() {
        LOG.debug("Initializing GalleryCardCustomPropertyImpl");
        LOG.debug("key: {}", key);
        LOG.debug("value: {}", value);
    }


    @JsonProperty("key")
    @Override
    public String getKey() {
        return key;
    }


    @JsonProperty("value")
    @Override
    public String getValue() {
        return value;
    }
}
