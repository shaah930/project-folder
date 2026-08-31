package com.kallista.core.models.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.ProductlistAllFiltersItem;
import com.kallista.core.models.ProductlistInlineFiltersItem;
import com.kallista.core.models.ProductlistModel;
import com.kallista.core.models.ProductlistSortByItem;

@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, adapters = { ProductlistModel.class,
		ComponentExporter.class }, resourceType = ProductlistModelImpl.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ProductlistModelImpl implements ProductlistModel {

	private static final Logger LOG = LoggerFactory.getLogger(ProductlistModelImpl.class);
	static final String RESOURCE_TYPE = "kallista/components/productlist/v1/productlist";

	@ValueMapValue(name = "categoryName")
	private String categoryName;

	@ValueMapValue(name = "categoryKey")
	private String categoryKey;

	@ValueMapValue(name = "attributeName")
	private String attributeName;

	@ValueMapValue(name = "attributeValue")
	private String attributeValue;

	@ValueMapValue(name = "newTab")
	private String newTab;

	@ValueMapValue(name = "xfLink")
	private String xfLink;

	@ValueMapValue(name = "promoLayout")
	private String promoLayout;

	@ValueMapValue(name = "alignDsktp")
	private String alignDsktp;

	@ValueMapValue(name = "alignMobile")
	private String alignMobile;

	@ValueMapValue(name = "defaultImgTag")
	private String defaultImgTag;

	@ValueMapValue(name = "hoverImageTag")
	private String hoverImageTag;

	@ValueMapValue(name = "newBadge")
	private String newBadge;

	@ValueMapValue(name = "sale")
	private String sale;

	@ChildResource
	private List<Resource> inlineFilter;

	@ChildResource
	private List<Resource> allFilter;

	@ChildResource
	private List<Resource> sortBy;

	@ValueMapValue(name = "defaultSort")
	private String defaultSort;

	@ScriptVariable
	private Page currentPage;

	@PostConstruct
	private void init() {
		LOG.debug("Initializing ProductlistModelImpl");

		if (categoryName == null || categoryName.isEmpty()) {
			categoryName = currentPage.getProperties().get("categoryName", "");
		}

		if (attributeName == null || attributeName.isEmpty()) {
			attributeName = currentPage.getProperties().get("attributeName", "");
		}

		if (attributeValue == null || attributeValue.isEmpty()) {
			attributeValue = currentPage.getProperties().get("attributeValue", "");
		}

		LOG.debug("categoryName: {}", categoryName);

		LOG.debug("categoryKey: {}", categoryKey);

		LOG.debug("attributeName: {}", attributeName);

		LOG.debug("attributeValue: {}", attributeValue);

		LOG.debug("xfLink: {}", xfLink);

		LOG.debug("promoLayout: {}", promoLayout);

		LOG.debug("alignDsktp: {}", alignDsktp);

		LOG.debug("alignMobile: {}", alignMobile);

		LOG.debug("defaultImgTag: {}", defaultImgTag);

		LOG.debug("hoverImageTag: {}", hoverImageTag);

		LOG.debug("newBadge: {}", newBadge);

		LOG.debug("sale: {}", sale);

		LOG.debug("inlineFilter: {}", inlineFilter != null ? inlineFilter.size() : 0);

		LOG.debug("allFilter: {}",allFilter != null ? allFilter.size() : 0);

		LOG.debug("sortBy count: {}", sortBy != null ? sortBy.size() : 0);

		LOG.debug("defaultSort: {}", defaultSort);

	}

	@JsonProperty("categoryName")
	@Override
	public String getCategoryname() {

		return categoryName != null ? categoryName : "";

	}

	@JsonProperty("categoryKey")
	@Override
	public String getCategorykey() {

		return categoryKey;

	}

	@JsonProperty("attributeName")
	@Override
	public String getAttributename() {

		return attributeName != null ? attributeName : "";

	}

	@JsonProperty("attributeValue")
	@Override
	public String getAttributevalue() {

		return attributeValue != null ? attributeValue : "";

	}

	@JsonProperty("newTab")
	@Override
	public String getNewtab() {

		return newTab;

	}

	@JsonProperty("xfLink")
	@Override
	public String getXflink() {

		return xfLink;

	}

	@JsonProperty("promoLayout")
	@Override
	public String getPromolayout() {

		return promoLayout;

	}

	@JsonProperty("alignDsktp")
	@Override
	public String getAligndsktp() {

		return alignDsktp;

	}

	@JsonProperty("alignMobile")
	@Override
	public String getAlignmobile() {

		return alignMobile;

	}

	@JsonProperty("defaultImgTag")
	@Override
	public String getDefaultimgtag() {

		return defaultImgTag;

	}

	@JsonProperty("hoverImageTag")
	@Override
	public String getHoverimagetag() {

		return hoverImageTag;

	}

	@JsonProperty("newBadge")
	@Override
	public String getNewbadge() {

		return newBadge;

	}

	@JsonProperty("sale")
	@Override
	public String getSale() {

		return sale;

	}

	@JsonProperty("inlineFilter")
	@Override
	public List<ProductlistInlineFiltersItem> getInlinefilter() {
		
		if (inlineFilter == null || inlineFilter.isEmpty()) {
			return Collections.emptyList();
		}

		List<ProductlistInlineFiltersItem> inlineFilterItems = new ArrayList<>();
		for (Resource inlineFilterResource : inlineFilter) {
			if (inlineFilterResource == null) {
				continue;
			}
			inlineFilterItems.add(new ProductlistInlineFiltersItemData(
					inlineFilterResource.getValueMap().get("inlineFilterLabel", String.class),
					inlineFilterResource.getValueMap().get("lwKey", String.class)));
		}
		return inlineFilterItems;
	}

	@JsonProperty("allFilter")
	@Override
	public List<ProductlistAllFiltersItem> getAllfilter() {

		if (allFilter == null || allFilter.isEmpty()) {
			return Collections.emptyList();
		}

		List<ProductlistAllFiltersItem> allFilterItems = new ArrayList<>();
		for (Resource allFilterResource : allFilter) {
			if (allFilterResource == null) {
				continue;
			}
			allFilterItems.add(new ProductlistAllFiltersItemData(
					allFilterResource.getValueMap().get("allFilterLabel", String.class),
					allFilterResource.getValueMap().get("lwKey", String.class)));
		}
		return allFilterItems;

	}

	@JsonProperty("sortBy")
	@Override
	public List<ProductlistSortByItem> getSortby() {

		if (sortBy == null || sortBy.isEmpty()) {
			return Collections.emptyList();
		}

		List<ProductlistSortByItem> sortByItems = new ArrayList<>();
		for (Resource sortByResource : sortBy) {
			if (sortByResource == null) {
				continue;
			}
			sortByItems.add(new ProductlistSortByItemData(
					sortByResource.getValueMap().get("sortByLabel", String.class),
					sortByResource.getValueMap().get("lwFieldValue", String.class)));
		}
		return sortByItems;

	}

	@JsonProperty("defaultSort")
	@Override
	public String getDefaultsort() {

		return defaultSort;

	}

	@Override
	public String getExportedType() {
		return RESOURCE_TYPE;
	}

	private static final class ProductlistSortByItemData implements ProductlistSortByItem {
		private final String sortByLabel;
		private final String lwFieldValue;

		private ProductlistSortByItemData(String sortByLabel, String lwFieldValue) {
			this.sortByLabel = sortByLabel;
			this.lwFieldValue = lwFieldValue;
		}

		@Override
		public String getSortByLabel() {
			return sortByLabel;
		}

		@Override
		public String getLwFieldValue() {
			return lwFieldValue;
		}
	}
	
	private static final class ProductlistInlineFiltersItemData implements ProductlistInlineFiltersItem {
		private final String inlineFilterLabel;
		private final String lwFieldValue;

		private ProductlistInlineFiltersItemData(String sortByLabel, String lwFieldValue) {
			this.inlineFilterLabel = sortByLabel;
			this.lwFieldValue = lwFieldValue;
		}

		@Override
		public String getInlineFilterLabel() {
			return inlineFilterLabel;
		}

		@Override
		public String getLwFieldValue() {
			return lwFieldValue;
		}
	}
	
	private static final class ProductlistAllFiltersItemData implements ProductlistAllFiltersItem {
		private final String allFilterLabel;
		private final String lwFieldValue;

		private ProductlistAllFiltersItemData(String sortByLabel, String lwFieldValue) {
			this.allFilterLabel = sortByLabel;
			this.lwFieldValue = lwFieldValue;
		}

		@Override
		public String getAllFilterLabel() {
			return allFilterLabel;
		}

		@Override
		public String getLwFieldValue() {
			return lwFieldValue;
		}
	}
}