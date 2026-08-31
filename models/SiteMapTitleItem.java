package com.kallista.core.models;

import java.util.List;

public interface SiteMapTitleItem {
	String getTabName();

	List<SiteMapColumnDetailsItem> getColumnDetails();
}
