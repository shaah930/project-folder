package com.kallista.core.models;

import java.util.List;

import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;

@ConsumerType
public interface FooterModel extends ComponentExporter {

    
    String getNewslettersignin();
    

    
    String getEmailplaceholder();
    

    
    String getCtalabel();

    String getCtaLink();

    String getCtaType();

    boolean isLinkNewTab();
    

    
    List<NavigationListItem> getNavigationlist();
    

    
    String getCopywrite();
    

    
    List<SubNavigationLinkItem> getSubnavigationlink();
    

    
    String getAlttext();
    
    String getBackGroundImage();
    
    List<IconListItem> getIconlist();
}