package com.kallista.core.models.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.FooterModel;
import com.kallista.core.models.NavigationListItem;
import com.kallista.core.models.IconListItem;
import com.kallista.core.models.SubNavigationLinkItem;

@Model(adaptables = {SlingHttpServletRequest.class},
       adapters = { FooterModel.class,ComponentExporter.class},
       resourceType = FooterModelImpl.RESOURCE_TYPE,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class FooterModelImpl implements FooterModel {

    private static final Logger LOG = LoggerFactory.getLogger(FooterModelImpl.class);
    static final String RESOURCE_TYPE = "kallista/components/Footer";


    
    @Inject
    @Via("resource")
    private String newsLetterSignIn;
    

    
    @Inject
    @Via("resource")
    private String emailPlaceholder;
    

    
    @Inject
    @Via("resource")
    private String ctaLabel;

    @Inject
    @Via("resource")
    private String ctaLink;

    @Inject
    @Via("resource")
    private String ctaType;

    @Inject
    @Via("resource")
    private boolean linkNewTab;
    

    
    @Inject
    @Via("resource")
    private List<NavigationListItem> navigationList;
    
    @Inject
    @Via("resource")
    private String Copywrite;
    
    @Inject
    @Via("resource")
    private List<SubNavigationLinkItem> subNavigationLink;
    
    @Inject
    @Via("resource")
    private String altText;
    
    @Inject
    @Via("resource")
    private String backGroundImage;
    
    @Inject
    @Via("resource")
    private List<IconListItem> iconList;

    @PostConstruct
    private void init() {
        LOG.info("Initializing FooterModelImpl");

    
        LOG.info("newsLetterSignIn: {}", newsLetterSignIn);
    

    
        LOG.info("emailPlaceholder: {}", emailPlaceholder);
    

    
        LOG.info("ctaLabel: {}", ctaLabel);
        LOG.info("ctaLink: {}", ctaLink);
        LOG.info("ctaType: {}", ctaType);
        LOG.info("linkNewTab: {}", linkNewTab);
    

    
        LOG.info("navigationList nested multifield size: {}", navigationList != null ? navigationList.size() : "null");
    

    
        LOG.info("Copywrite: {}", Copywrite);
    

    
        LOG.info("subNavigationLink nested multifield size: {}", subNavigationLink != null ? subNavigationLink.size() : "null");
    

    
        LOG.info("altText: {}", altText);
        
        LOG.info("backGroundImage: {}", backGroundImage);
       LOG.info("iconList nested multifield size: {}", iconList != null ? iconList.size() : "null");
    

    }


    
    @JsonProperty("newsLetterSignIn")
    @Override
    public String getNewslettersignin() {
        
        return newsLetterSignIn;
        
    }
    

    
    @JsonProperty("emailPlaceholder")
    @Override
    public String getEmailplaceholder() {
        
        return emailPlaceholder;
        
    }
    

    
    @JsonProperty("ctaLabel")
    @Override
    public String getCtalabel() {
        
        return ctaLabel;
        
    }

    @JsonProperty("ctaLink")
    @Override
    public String getCtaLink() {
        return ctaLink;
    }

    @JsonProperty("ctaType")
    @Override
    public String getCtaType() {
        return ctaType;
    }

    @JsonProperty("linkNewTab")
    @Override
    public boolean isLinkNewTab() {
        return linkNewTab;
    }
    

    
    @JsonProperty("navigationList")
    @Override
    public List<NavigationListItem> getNavigationlist() {
        return navigationList != null ? navigationList : Collections.emptyList();
    }
    

    
    @JsonProperty("Copywrite")
    @Override
    public String getCopywrite() {
        
        return Copywrite;
        
    }
    

    
    @JsonProperty("subNavigationLink")
    @Override
    public List<SubNavigationLinkItem> getSubnavigationlink() {
        return subNavigationLink != null ? subNavigationLink : Collections.emptyList();
    }
    

    
    @JsonProperty("altText")
    @Override
    public String getAlttext() {
        
        return altText;
        
    }
    
    @JsonProperty("backGroundImage")
    @Override
    public String getBackGroundImage() {
        
        return backGroundImage;
        
    }
    @JsonProperty("iconList")
    @Override
    public List<IconListItem> getIconlist() {
        return iconList != null ? iconList : Collections.emptyList();
    }


    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }
}