package dsm.foundation.core.models.impl.v1;

public class ContentCta {
	private final String linkLabel;
	private final String link;

	public ContentCta(String linkLabel, String link) {
		this.linkLabel = linkLabel;
		this.link = link;
	}

	public String getLinkLabel() {
		return linkLabel;
	}

	public String getLink() {
		return link;
	}
}
