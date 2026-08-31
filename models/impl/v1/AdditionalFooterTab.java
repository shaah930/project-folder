package dsm.foundation.core.models.impl.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdditionalFooterTab {
	@JsonProperty("subTabs")
	private final List<AdditionalFooterSubTab> subTabs;
	@JsonProperty("appliedCssClassNames")
	private final String appliedCssClassNames;
	@JsonProperty("facebook")
	private final String facebook;
	@JsonProperty("linkFacebook")
	private final String linkFacebook;
	@JsonProperty("linkLabelFacebook")
	private final String linkLabelFacebook;
	@JsonProperty("pinterest")
	private final String pinterest;
	@JsonProperty("linkPinterest")
	private final String linkPinterest;
	@JsonProperty("linkLabelPinterest")
	private final String linkLabelPinterest;
	@JsonProperty("instagram")
	private final String instagram;
	@JsonProperty("linkInstagram")
	private final String linkInstagram;
	@JsonProperty("linkLabelInstagram")
	private final String linkLabelInstagram;
	@JsonProperty("twitter")
	private final String twitter;
	@JsonProperty("linkTwitter")
	private final String linkTwitter;
	@JsonProperty("linkLabelTwitter")
	private final String linkLabelTwitter;
	@JsonProperty("youtube")
	private final String youtube;
	@JsonProperty("linkYoutube")
	private final String linkYoutube;
	@JsonProperty("linkLabelYoutube")
	private final String linkLabelYoutube;
	@JsonProperty("houzz")
	private final String houzz;
	@JsonProperty("linkHouzz")
	private final String linkHouzz;
	@JsonProperty("linkLabelHouzz")
	private final String linkLabelHouzz;
	@JsonProperty("linkedin")
	private final String linkedin;
	@JsonProperty("linkLinkedin")
	private final String linkLinkedin;
	@JsonProperty("linkLabelLinkedin")
	private final String linkLabelLinkedin;
	@JsonProperty("email")
	private final String email;
	@JsonProperty("linkEmail")
	private final String linkEmail;
	@JsonProperty("linkLabelEmail")
	private final String linkLabelEmail;
	@JsonProperty("tiktok")
	private final String tiktok;
	@JsonProperty("linkTiktok")
	private final String linkTiktok;
	@JsonProperty("linkLabelTiktok")
	private final String linkLabelTiktok;
	@JsonProperty
	private final String copyright;

	@JsonProperty
	private final String legalDisclaimer;

	public AdditionalFooterTab(List<AdditionalFooterSubTab> subTabs, String appliedCssClassNames, String facebook,
			String linkFacebook, String linkLabelFacebook, String pinterest, String linkPinterest,
			String linkLabelPinterest, String instagram, String linkInstagram, String linkLabelInstagram,
			String twitter, String linkTwitter, String linkLabelTwitter, String youtube, String linkYoutube,
			String linkLabelYoutube, String houzz, String linkHouzz, String linkLabelHouzz, String linkedin,
			String linkLinkedin, String linkLabelLinkedin, String email, String linkEmail, String linkLabelEmail,
			String tiktok, String linkTiktok, String linkLabelTiktok, String copyright, String legalDisclaimer) {
		this.subTabs = subTabs;
		this.appliedCssClassNames = appliedCssClassNames;
		this.facebook = facebook;
		this.linkFacebook = linkFacebook;
		this.linkLabelFacebook = linkLabelFacebook;
		this.pinterest = pinterest;
		this.linkPinterest = linkPinterest;
		this.linkLabelPinterest = linkLabelPinterest;
		this.instagram = instagram;
		this.linkInstagram = linkInstagram;
		this.linkLabelInstagram = linkLabelInstagram;
		this.twitter = twitter;
		this.linkTwitter = linkTwitter;
		this.linkLabelTwitter = linkLabelTwitter;
		this.youtube = youtube;
		this.linkYoutube = linkYoutube;
		this.linkLabelYoutube = linkLabelYoutube;
		this.houzz = houzz;
		this.linkHouzz = linkHouzz;
		this.linkLabelHouzz = linkLabelHouzz;
		this.linkedin = linkedin;
		this.linkLinkedin = linkLinkedin;
		this.linkLabelLinkedin = linkLabelLinkedin;
		this.email = email;
		this.linkEmail = linkEmail;
		this.linkLabelEmail = linkLabelEmail;
		this.tiktok = tiktok;
		this.linkTiktok = linkTiktok;
		this.linkLabelTiktok = linkLabelTiktok;
		this.copyright = copyright;
		this.legalDisclaimer = legalDisclaimer;
	}

	public List<AdditionalFooterSubTab> getSubTabs() {
		return subTabs;
	}

	public String getAppliedCssClassNames() {
		return appliedCssClassNames;
	}

	public String getFacebook() {
		return facebook;
	}

	public String getLinkFacebook() {
		return linkFacebook;
	}

	public String getLinkLabelFacebook() {
		return linkLabelFacebook;
	}

	public String getPinterest() {
		return pinterest;
	}

	public String getLinkPinterest() {
		return linkPinterest;
	}

	public String getLinkLabelPinterest() {
		return linkLabelPinterest;
	}

	public String getInstagram() {
		return instagram;
	}

	public String getLinkInstagram() {
		return linkInstagram;
	}

	public String getLinkLabelInstagram() {
		return linkLabelInstagram;
	}

	public String getTwitter() {
		return twitter;
	}

	public String getLinkTwitter() {
		return linkTwitter;
	}

	public String getLinkLabelTwitter() {
		return linkLabelTwitter;
	}

	public String getYoutube() {
		return youtube;
	}

	public String getLinkYoutube() {
		return linkYoutube;
	}

	public String getLinkLabelYoutube() {
		return linkLabelYoutube;
	}

	public String getHouzz() {
		return houzz;
	}

	public String getLinkHouzz() {
		return linkHouzz;
	}

	public String getLinkLabelHouzz() {
		return linkLabelHouzz;
	}

	public String getLinkedin() {
		return linkedin;
	}

	public String getLinkLinkedin() {
		return linkLinkedin;
	}

	public String getLinkLabelLinkedin() {
		return linkLabelLinkedin;
	}

	public String getEmail() {
		return email;
	}

	public String getLinkEmail() {
		return linkEmail;
	}

	public String getLinkLabelEmail() {
		return linkLabelEmail;
	}

	public String getTiktok() {
		return tiktok;
	}

	public String getLinkTiktok() {
		return linkTiktok;
	}

	public String getLinkLabelTiktok() {
		return linkLabelTiktok;
	}

	public String getCopyright() {
		return copyright;
	}

	public String getLegalDisclaimer() {
		return legalDisclaimer;
	}
}
