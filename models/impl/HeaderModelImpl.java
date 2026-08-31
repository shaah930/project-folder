package com.kallista.core.models.impl;

import com.kallista.core.models.HeaderModel;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;  
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.Exporter;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.sling.models.annotations.Via;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Collections;
import javax.inject.Inject;
import com.kallista.core.models.TitleItem;
import com.kallista.core.models.UtilityDetailsItem;

import dsm.foundation.core.utils.AssetUtils;

import com.kallista.core.models.CtaDetailsItem;
import com.kallista.core.models.IconListItem;



@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
       adapters = { HeaderModel.class,ComponentExporter.class},
       resourceType = HeaderModelImpl.RESOURCE_TYPE,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class HeaderModelImpl implements HeaderModel {

    private static final Logger LOG = LoggerFactory.getLogger(HeaderModelImpl.class);
    static final String RESOURCE_TYPE = "kallista/components/header";

    @Inject
    PublishUtils publishUtils;

    @SlingObject
    private Resource resource;

    @Inject
    @Via("resource")
    private List<TitleItem> title;

    @ValueMapValue(name = "altText")
    private String altText;
    
    @ValueMapValue(name = "fileReference")
    private String logoImage;
    
    @Inject
    @Via("resource")
    private List<UtilityDetailsItem> utilityDetails;

    @Inject
    @Via("resource")
    private List<CtaDetailsItem> additionalHeaderDetails;

    @Inject
    @Via("resource")
    private List<IconListItem> iconList;


    @PostConstruct
    private void init() {
        LOG.debug("Initializing HeaderModelImpl");
    
        LOG.debug("title nested multifield size: {}", title != null ? title.size() : "null");
        
        LOG.debug("title nested multifield size: {}", title != null ? title.size() : "null");
    
        LOG.debug("altText: {}", altText);
    
        LOG.debug("logoImage: {}", AssetUtils.getScene7AssetPath(logoImage, resource, publishUtils));
   
        LOG.debug("utilityDetails nested multifield size: {}", utilityDetails != null ? utilityDetails.size() : "null");

        LOG.debug("additionalHeaderDetails nested multifield size: {}",
                additionalHeaderDetails != null ? additionalHeaderDetails.size() : "null");
        LOG.info("iconList nested multifield size: {}", iconList != null ? iconList.size() : "null");
    }
    
    @JsonProperty("title")
    @Override
    public List<TitleItem> getTitle() {
        return title != null ? title : Collections.emptyList();
    }
    
    @JsonProperty("altText")
    @Override
    public String getAlttext() {        
        return altText;        
    }
    
    @JsonProperty("logoImage")
    @Override
    public String getLogoimage() {        
        // For image fields, return the fileReference if available, otherwise the direct field value
        return  AssetUtils.getScene7AssetPath(logoImage, resource, publishUtils); 
    }
    
    @JsonProperty("utilityDetails")
    @Override
    public List<UtilityDetailsItem> getUtilitydetails() {
        return utilityDetails != null ? utilityDetails : Collections.emptyList();
    }

    @JsonProperty("additionalHeaderDetails")
    @Override
    public List<CtaDetailsItem> getAdditionalHeaderDetails() {
        return additionalHeaderDetails != null ? additionalHeaderDetails : Collections.emptyList();
    }

    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }

    @JsonProperty("iconList")
    @Override
    public List<IconListItem> getIconlist() {
        return iconList != null ? iconList : Collections.emptyList();
    }
}