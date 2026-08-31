package dsm.foundation.core.models.impl.v1;

import java.util.List;

public class MainFooterQuadrant {
	private final String title;
	private final String titleLink;
	private final String desc;
	private final String radioNavigation;
	private final List<KhsFooterMainItem> items;

	public MainFooterQuadrant(String title, String titleLink, String desc, String radioNavigation, List<KhsFooterMainItem> items) {
		this.title = title;
		this.titleLink = titleLink;
		this.desc = desc;
		this.radioNavigation = radioNavigation;
		this.items = items;
	}

	public String getTitle() {
		return title;
	}

	public String getTitleLink() {
		return titleLink;
	}

	public String getDesc() {
		return desc;
	}

	public String getRadioNavigation() {
		return radioNavigation;
	}

	public List<KhsFooterMainItem> getItems() {
		return items;
	}
}
