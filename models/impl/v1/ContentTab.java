package dsm.foundation.core.models.impl.v1;

import java.util.List;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import dsm.foundation.core.utils.AssetUtils;

// Content Tab POJO
public class ContentTab {
	private final Resource resource;
	private final PublishUtils publishUtils;
	private final String mod, variant;
	private final String src, alt;
	@ChildResource
	private final List<Contact> contact;
	@ChildResource
	private List<DescriptionItem> description;
	private final List<ContentCta> ctas;

	public ContentTab(String mod, String variant, String src, String alt, List<Contact> contact, List<DescriptionItem> description, List<ContentCta> ctas, Resource resource, PublishUtils publishUtils) {
		this.mod = mod;
		this.variant = variant;
		this.src = src;
		this.alt = alt;
		this.contact = contact;
		this.description = description;
		this.ctas = ctas;
		this.resource = resource;
		this.publishUtils = publishUtils;
	}

	public String getMod() {
		return mod;
	}
	public String getVariant() {
		return variant;
	}
	@JsonProperty("logo")
	public String getSrc() {
		try {
			if (src != null && resource != null && publishUtils != null) {
				String resolved = AssetUtils.getScene7AssetPath(src, resource, publishUtils);
				return resolved != null ? resolved : src;
			}
		} catch (Exception e) {
			// Log or ignore, but never throw
		}
		return src;
	}

	public String getAlt() {
		return alt;
	}

	public List<Contact> getContact() {
		return contact;
	}

	public List<DescriptionItem> getDescription() {
		return description;
	}

	public List<ContentCta> getCtas() {
		return ctas;
	}
}
