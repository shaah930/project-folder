package com.kallista.core.models.impl;

import com.kallista.core.models.IconListItem;

import dsm.foundation.core.utils.AssetUtils;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.Exporter;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       adapters = IconListItem.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, 
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class IconListItemImpl implements IconListItem {
    
    private static final Logger LOG = LoggerFactory.getLogger(IconListItemImpl.class);

    @Inject
    PublishUtils publishUtils;

    @SlingObject
    private Resource resource;
    
    @ValueMapValue(name = "socialIcon")
    private String socialIcon;
    
    @ValueMapValue(name = "socialLink")
    private String socialLink;
 
    @ValueMapValue(name = "iconAlt")
    private String iconAlt;

 
    @JsonProperty("socialIcon")
    @Override
    public String getSocialIcon() {
        return  AssetUtils.getScene7AssetPath(socialIcon, resource, publishUtils); 
    }
    

    @JsonProperty("socialLink")
    @Override
    public String getSocialLink() {
        return socialLink;
    }

    @JsonProperty("iconAlt")
    @Override
    public String getIconAlt() {
        return iconAlt;
    }
}