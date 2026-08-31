package dsm.foundation.core.models.impl.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { KhsFooterModel.class,
		ComponentExporter.class }, resourceType = KhsFooterModel.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KhsFooterModel implements ComponentExporter {
	// Content Tab POJO
	private ContentTab contentTab;
	public static final String RESOURCE_TYPE = "aem-dsm-foundation/components/footer/v1/footer";
	public static final String ID_SEPARATOR = "-";
	protected static final Logger LOGGER = LoggerFactory.getLogger(KhsFooterModel.class);

	// Main Footer fields
	@Self
	private SlingHttpServletRequest request;

	@Inject
	private Resource resource;

	@Inject
	private PublishUtils publishUtils;

	@ValueMapValue
	@Via("resource")
	private String quadrantTitleOne;
	@ValueMapValue
	@Via("resource")
	private String quadrantTitleOnelink;
	@ValueMapValue
	@Via("resource")
	private String quadrantTitleTwo;
	@ValueMapValue
	@Via("resource")
	private String quadrantTitleTwolink;
	@ValueMapValue
	@Via("resource")
	private String quadrantTitleThree;
	@ValueMapValue
	@Via("resource")
	private String quadrantTitleThreelink;
	@ValueMapValue
	@Via("resource")
	private String quadrantTitleFour;
	@ValueMapValue
	@Via("resource")
	private String quadrantTitleFourlink;
	@ValueMapValue
	@Via("resource")
	private String quadrantTitleFive;
	@ValueMapValue
	@Via("resource")
	private String quadrantTitleFivelink;
	@ValueMapValue
	@Via("resource")
	private String radioNavigationOne;
	@ValueMapValue
	@Via("resource")
	private String radioNavigationTwo;
	@ValueMapValue
	@Via("resource")
	private String radioNavigationThree;
	@ValueMapValue
	@Via("resource")
	private String radioNavigationFour;
	@ValueMapValue
	@Via("resource")
	private String radioNavigationFive;
	@ValueMapValue
	@Via("resource")
	private String quadrantDescOne;
	@ValueMapValue
	@Via("resource")
	private String quadrantDescTwo;
	@ValueMapValue
	@Via("resource")
	private String quadrantDescThree;
	@ValueMapValue
	@Via("resource")
	private String quadrantDescFour;
	@ValueMapValue
	@Via("resource")
	private String quadrantDescFive;
	@ValueMapValue
	@Via("resource")
	private String viewStore;
	@ValueMapValue
	@Via("resource")
	private String viewStoreLink;

	// Additional Footer fields
	@ScriptVariable
	private Page currentPage;
	@ValueMapValue
	@Via("resource")
	private String facebook;
	@SlingObject
	private Resource currentResource;
	@ValueMapValue
	@Via("resource")
	private String linkFacebook;
	@ValueMapValue
	@Via("resource")
	private String linkLabelFacebook;
	@ValueMapValue
	@Via("resource")
	private String pinterest;
	@ValueMapValue
	@Via("resource")
	private String linkPinterest;
	@ValueMapValue
	@Via("resource")
	private String linkLabelPinterest;
	@ValueMapValue
	@Via("resource")
	private String instagram;
	@ValueMapValue
	@Via("resource")
	private String linkInstagram;
	@ValueMapValue
	@Via("resource")
	private String linkLabelInstagram;
	@ValueMapValue
	@Via("resource")
	private String twitter;
	@ValueMapValue
	@Via("resource")
	private String linkTwitter;
	@ValueMapValue
	@Via("resource")
	private String linkLabelTwitter;
	@ValueMapValue
	@Via("resource")
	private String youtube;
	@ValueMapValue
	@Via("resource")
	private String linkYoutube;
	@ValueMapValue
	@Via("resource")
	private String linkLabelYoutube;
	@ValueMapValue
	@Via("resource")
	private String houzz;
	@ValueMapValue
	@Via("resource")
	private String linkHouzz;
	@ValueMapValue
	@Via("resource")
	private String linkLabelHouzz;
	@ValueMapValue
	@Via("resource")
	private String linkedin;
	@ValueMapValue
	@Via("resource")
	private String linkLinkedin;
	@ValueMapValue
	@Via("resource")
	private String email;
	@ValueMapValue
	@Via("resource")
	private String linkEmail;
	@ValueMapValue
	@Via("resource")
	private String linkLabelEmail;
	@ValueMapValue
	@Via("resource")
	private String tiktok;
	@ValueMapValue
	@Via("resource")
	private String linkTiktok;
	@ValueMapValue
	@Via("resource")
	private String linkLabelTiktok;
	@ValueMapValue
	@Via("resource")
	private String linkLabelLinkedin;
	@ValueMapValue
	@Via("resource")
	private String copyright;

	@ValueMapValue
	@Via("resource")
	private String legalDisclaimer;

	@ChildResource
	private List<Contact> contact;

	@ChildResource
	private List<DescriptionItem> description;

	private String appliedCssClassNames;
	private List<MainFooterQuadrant> mainFooterQuadrants = Collections.emptyList();
	private AdditionalFooterTab additionalFooterTab;

	@PostConstruct
	private void init() {
		// Content Tab POJO population
		List<ContentCta> ctas = new ArrayList<>();
		Resource ctasNode = resource.getChild("ctas");
		if (ctasNode != null) {
			for (Resource itemRes : ctasNode.getChildren()) {
				ValueMap vm = itemRes.getValueMap();
				String linkLabel = vm.get("linkLabel", String.class);
				String link = vm.get("link", String.class);
				if (linkLabel != null || link != null) {
					ctas.add(new ContentCta(linkLabel, link));
				}
			}
		}
		ValueMap contentvm = resource.getValueMap();
		// Fetch logo from child node 'logo/fileReference' if present, else fallback to
		// direct property
		String logoPath = contentvm.get("logo", String.class);
		contentTab = new ContentTab(contentvm.get("mod", String.class), contentvm.get("variant", String.class), contentvm.get("src", String.class), contentvm.get("alt", String.class),
			contact, description, ctas, resource, publishUtils);
		// Main Footer Quadrants
		mainFooterQuadrants = new ArrayList<>();
		mainFooterQuadrants.add(createQuadrant("quadrantOne", quadrantTitleOne, quadrantTitleOnelink, quadrantDescOne, radioNavigationOne,
				"brandNameOne", "brandLinkOne", "newTabOne"));
		mainFooterQuadrants.add(createQuadrant("quadrantTwo", quadrantTitleTwo, quadrantTitleTwolink, quadrantDescTwo, radioNavigationTwo,
				"brandNameTwo", "brandLinkTwo", "newTabTwo"));
		mainFooterQuadrants.add(createQuadrant("quadrantThree", quadrantTitleThree, quadrantTitleThreelink, quadrantDescThree,
				radioNavigationThree, "brandNameThree", "brandLinkThree", "newTabThree"));
		mainFooterQuadrants.add(createQuadrant("quadrantFour", quadrantTitleFour, quadrantTitleFourlink, quadrantDescFour, radioNavigationFour,
				"brandNameFour", "brandLinkFour", "newTabFour"));
		mainFooterQuadrants.add(createQuadrant("quadrantFive", quadrantTitleFive, quadrantTitleFivelink, quadrantDescFive, radioNavigationFive,
				"brandNameFive", "brandLinkFive", "newTabFive"));

		// Additional Footer: Read items from 'products' multifield (as per dialog)
		List<AdditionalFooterSubTab> additionalFooterSubTabs = new ArrayList<>();
		Resource productsNode = resource.getChild("products");
		List<KhsFooterAdditionalItem> allAdditionalItems = new ArrayList<>();
		if (productsNode != null) {
			for (Resource itemRes : productsNode.getChildren()) {
				ValueMap vm = itemRes.getValueMap();
				if (vm.containsKey("linkLabel") || vm.containsKey("link")) {
					allAdditionalItems.add(new KhsFooterAdditionalItem(itemRes));
				}
			}
		}
		additionalFooterSubTabs.add(new AdditionalFooterSubTab("Links", allAdditionalItems));
		additionalFooterTab = new AdditionalFooterTab(additionalFooterSubTabs, getAppliedCssClassNames(), facebook,
				linkFacebook, linkLabelFacebook, pinterest, linkPinterest, linkLabelPinterest, instagram, linkInstagram,
				linkLabelInstagram, twitter, linkTwitter, linkLabelTwitter, youtube, linkYoutube, linkLabelYoutube,
				houzz, linkHouzz, linkLabelHouzz, linkedin, linkLinkedin, linkLabelLinkedin, email, linkEmail,
				linkLabelEmail, tiktok, linkTiktok, linkLabelTiktok, copyright, legalDisclaimer);
	}

	private MainFooterQuadrant createQuadrant(String nodeName, String title, String titleLink, String desc, String radio,
			String brandNameKey, String brandLinkKey, String newTabKey) {
		Resource itemsNode = resource.getChild(nodeName);
		List<KhsFooterMainItem> items = new ArrayList<>();
		if (itemsNode != null) {
			for (Resource itemRes : itemsNode.getChildren()) {
				items.add(new KhsFooterMainItem(itemRes, brandNameKey, brandLinkKey, newTabKey));
			}
		}
		return new MainFooterQuadrant(title, titleLink, desc, radio, items);
	}

	private AdditionalFooterSubTab createAdditionalSubTab(String titleKey, String itemsNodeName) {
		// Deprecated: not used anymore, logic moved to init() for 'products' multifield
		return new AdditionalFooterSubTab(titleKey, new ArrayList<>());
	}

	private List<KhsFooterMainItem> populateMainItems(String quadrant, String brandNameKey, String brandLinkKey,
			String newTabKey) {
		Resource itemsNode = resource.getChild(quadrant);
		List<KhsFooterMainItem> items = new ArrayList<>();
		if (itemsNode != null) {
			for (Resource itemRes : itemsNode.getChildren()) {
				items.add(new KhsFooterMainItem(itemRes, brandNameKey, brandLinkKey, newTabKey));
			}
		}
		return items;
	}

	// ...existing code...

	// Properly placed methods for additional footer fields
	// Removed: getAdditionalItems() as additionalFooterSubTabs is now local to
	// init().

	public String getAppliedCssClassNames() {
		return appliedCssClassNames;
	}

	// Main Footer POJO Getter
	public List<MainFooterQuadrant> getMainFooterQuadrants() {
		return mainFooterQuadrants;
	}

	// Additional Footer POJO Getter
	// Removed: public List<AdditionalFooterSubTab> getAdditionalFooterSubTabs() {
	// return additionalFooterSubTabs; }
	public AdditionalFooterTab getAdditionalFooterTab() {
		return additionalFooterTab;
	}

	// Content Tab getter for JSON export
	public ContentTab getContentTab() {
		return contentTab;
	}

	@Override
	public String getExportedType() {
		return resource != null ? resource.getResourceType() : "";
	}
	// Removed old tab getters; now use POJO-based grouping only.
}
