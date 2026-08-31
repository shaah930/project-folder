package com.kallista.core.models;

import java.util.List;

import com.adobe.cq.export.json.ComponentExporter;

public interface ProductlistModel extends ComponentExporter {

	String getCategoryname();

	String getCategorykey();

	String getAttributename();

	String getAttributevalue();

	String getXflink();

	String getPromolayout();

	String getAligndsktp();

	String getAlignmobile();

	String getDefaultimgtag();

	String getHoverimagetag();

	String getNewbadge();

	String getSale();

	String getNewtab();

	List<ProductlistInlineFiltersItem> getInlinefilter();

	List<ProductlistAllFiltersItem> getAllfilter();

	List<ProductlistSortByItem> getSortby();

	String getDefaultsort();

}