package dsm.foundation.core.models.impl.v1;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KhsFooterAdditionalItem implements ListItem {
	private final String linkLabel;
	private final String link;
	private final String newTab;
	private final boolean linkRef;
	private final String classLabel;
	private final String idLabel;

	public KhsFooterAdditionalItem(Resource itemRes) {
		ValueMap properties = itemRes.getValueMap();
		linkLabel = properties.get("linkLabel", String.class);
		link = properties.get("link", String.class);
		newTab = properties.get("newTab", String.class);
		classLabel = properties.get("classLabel", String.class);
		idLabel = properties.get("idLabel", String.class);
		linkRef = false; // logic can be added if needed
	}

	public String getLinkLabel() {
		return linkLabel;
	}

	public String getClassLabel() {
		return classLabel;
	}

	public String getIdLabel() {
		return idLabel;
	}

	public String getNewTab() {
		return newTab;
	}

	@JsonProperty("link")
	public String getAuthoredLink() {
		return link;
	}

	public boolean getLinkRef() {
		return linkRef;
	}
}
