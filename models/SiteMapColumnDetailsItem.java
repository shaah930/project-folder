package com.kallista.core.models;

import java.util.List;

public interface SiteMapColumnDetailsItem {

    String getHeading();
    
    String getHeadingLink();

    List<SubHeadingItem> getSubHeading();
}
