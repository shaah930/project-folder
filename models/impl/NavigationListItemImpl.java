package com.kallista.core.models.impl;

import com.kallista.core.models.NavigationListItem;
import com.kallista.core.models.NavigationLinkItem;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import com.adobe.cq.export.json.ExporterConstants;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.Exporter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.sling.models.annotations.Via;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       adapters = NavigationListItem.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, 
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class NavigationListItemImpl implements NavigationListItem {
    
    private static final Logger LOG = LoggerFactory.getLogger(NavigationListItemImpl.class);


    
    @ValueMapValue(name = "heading")
    private String heading;
    


    

    @ChildResource(name = "navigationLink")
    private List<NavigationLinkItem> navigationLink;
    


    @PostConstruct
    private void init() {
        LOG.debug("Initializing NavigationListItemImpl");

        
        LOG.debug("heading: {}", heading);
        

        
        LOG.debug("navigationLink: {}", navigationLink);
        

    }


    
    @JsonProperty("heading")
    @Override
    public String getHeading() {
        return heading;
    }
    


    

    @JsonProperty("navigationLink")
    @Override
    public List<NavigationLinkItem> getNavigationLink() {
        return navigationLink != null ? navigationLink : Collections.emptyList();
    }
    


}