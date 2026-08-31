package com.kallista.core.models;

import java.util.List;

import com.adobe.cq.export.json.ComponentExporter;

public interface BannerLinkProductsModel extends ComponentExporter{
	String getScene7Url();
	
	List<String> getProductIds();
	
	Long getBannerPosition();
	
	String getTitle();
	
	String getDescriptionText();
	
	String getLabel();
	
	String getLink();
	
}
