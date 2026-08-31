package com.kallista.core.models.impl;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.ProductlistAllFiltersItem;

@Model(adaptables = Resource.class, adapters = ProductlistAllFiltersItem.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductlistAllFiltersItemImpl implements ProductlistAllFiltersItem {

	@ValueMapValue
	private String allFilterLabel;

	@ValueMapValue
	private String lwFieldValue;

	@Override
    @JsonProperty("allFilterLabel")
	public String getAllFilterLabel() {
		return allFilterLabel;
	}

	@Override
    @JsonProperty("lwFieldValue")
	public String getLwFieldValue() {
		return lwFieldValue;
	}
	
	
}
