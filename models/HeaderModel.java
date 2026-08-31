package com.kallista.core.models;

import java.util.List;
import java.util.Map;
import com.adobe.cq.export.json.ComponentExporter;

public interface HeaderModel extends ComponentExporter {

  
    List<TitleItem> getTitle();    
    String getAlttext();
    String getLogoimage();
    List<UtilityDetailsItem> getUtilitydetails();
    List<CtaDetailsItem> getAdditionalHeaderDetails();
    List<IconListItem> getIconlist();
    

}