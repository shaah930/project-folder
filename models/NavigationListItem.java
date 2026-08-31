package com.kallista.core.models;

import java.util.List;

public interface NavigationListItem {

    
    String getHeading();

    List<NavigationLinkItem> getNavigationLink();
    

}