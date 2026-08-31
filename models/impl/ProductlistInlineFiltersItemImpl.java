package com.kallista.core.models.impl;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.ProductlistInlineFiltersItem;

@Model(adaptables = Resource.class, adapters = ProductlistInlineFiltersItem.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductlistInlineFiltersItemImpl implements ProductlistInlineFiltersItem {

	@ValueMapValue
	private String inlineFilterLabel;

	@ValueMapValue
	private String lwFieldValue;

	@Override
    @JsonProperty("inlineFilterLabel")
	public String getInlineFilterLabel() {
		return inlineFilterLabel;
	}

	@Override
    @JsonProperty("lwFieldValue")
	public String getLwFieldValue() {
		return lwFieldValue;
	}

}
