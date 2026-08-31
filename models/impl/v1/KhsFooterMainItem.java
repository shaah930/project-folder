package dsm.foundation.core.models.impl.v1;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import com.adobe.cq.wcm.core.components.models.ListItem;
import dsm.foundation.core.constants.DsmConstants;
import org.apache.commons.lang3.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KhsFooterMainItem implements ListItem {
	private final String brandName;
	private final String brandLink;
	private final boolean newTab;
	private final boolean brandNameRef;

	public KhsFooterMainItem(Resource itemRes, String brandNameKey, String brandLinkKey, String newTabKey) {
		ValueMap properties = itemRes.getValueMap();
		this.brandName = properties.get(brandNameKey, String.class);
		String link = properties.get(brandLinkKey, String.class);
		boolean ref = true;
		Pattern regex = Pattern.compile("\\.[^.\\\\]+$", Pattern.MULTILINE);
		Matcher matcher = regex.matcher(link != null ? link : StringUtils.EMPTY);
		if (link != null && link.startsWith(DsmConstants.CONTENT_PATH) && !link.contains(DsmConstants.EXTENTION)
				&& !matcher.find()) {
			ref = false;
			link = link + DsmConstants.EXTENTION;
		}
		this.brandLink = link != null ? link : StringUtils.EMPTY;
		this.brandNameRef = ref;
		Boolean tab = properties.get(newTabKey, Boolean.class);
		this.newTab = tab != null ? tab : false;
	}

	public String getBrandName() {
		return brandName;
	}

	public String getBrandLink() {
		return brandLink;
	}

	public boolean isNewTab() {
		return newTab;
	}

	public boolean getBrandNameRef() {
		return brandNameRef;
	}
}
