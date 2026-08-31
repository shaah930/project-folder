package com.kallista.core.models.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.constants.KallistaConstants;
import com.kallista.core.models.BannerLinkProductsModel;
import com.kallista.core.utils.Scene7Utils;

@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, adapters = { BannerLinkProductsModel.class,
		ComponentExporter.class }, resourceType = BannerLinkProductsModelImpl.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class BannerLinkProductsModelImpl implements BannerLinkProductsModel {

	private static final Logger LOG = LoggerFactory.getLogger(BannerLinkProductsModelImpl.class);

	static final String RESOURCE_TYPE = "kallista/components/bannerLinkProducts";

	/**
	 * DAM asset path stored by the dialog's
	 * {@code fileReferenceParameter=./imageFileReference}. Used only internally to
	 * resolve the Scene7 URL; excluded from JSON output.
	 */
	@JsonIgnore
	@ValueMapValue
	private String imageFileReference;

	/**
	 * Multi-value JCR property {@code productID} authored via the multifield
	 * dialog. Stored as a {@code String[]} multi-value property on the component
	 * node.
	 */
	@ValueMapValue(name = "productID")
	private List<String> productIds;

	/** Numeric banner position used by the front-end layer to order banners. */
	@ValueMapValue
	private Long bannerPosition;

	@ValueMapValue
	private String title;

	@ValueMapValue
	private String descriptionText;

	@ValueMapValue
	private String label;

	@ValueMapValue
	private String link;

	/**
	 * Request-scoped {@link ResourceResolver} used to navigate to the DAM asset
	 * metadata node during Scene7 URL resolution. Excluded from JSON output.
	 */
	@JsonIgnore
	@SlingObject
	private ResourceResolver resourceResolver;

	/**
	 * Fully-qualified Scene7 CDN URL computed in {@link #init()} from the
	 * {@code dam:scene7Domain} and {@code dam:scene7File} metadata properties of
	 * the authored DAM asset.
	 */
	private String scene7Url;

	@OSGiService
	PublishUtils publishUtils;

	@PostConstruct
	protected void init() {
		if (StringUtils.isNotBlank(imageFileReference)) {
			scene7Url = Scene7Utils.getScene7AssetPath(imageFileReference, resourceResolver, publishUtils);
			if (scene7Url.isEmpty()) {
				LOG.debug(
						"BannerLinkProducts: Scene7 URL could not be resolved for asset '{}'. "
								+ "Verify that the asset has been published to Scene7 and that "
								+ "'{}' / '{}' are present on its metadata node.",
						imageFileReference, KallistaConstants.PN_SCENE7_DOMAIN, KallistaConstants.PN_SCENE7_FILE);
			}
		}
		if (productIds == null) {
			productIds = Collections.emptyList();
		}
	}

	/**

	/**
	 * Returns the Scene7 CDN URL for the authored DAM asset, constructed from
	 * {@code dam:scene7Domain} and {@code dam:scene7File} on the asset's metadata
	 * node, or {@code null} when the asset has not been published to Scene7 or no
	 * image has been authored.
	 *
	 * <p>
	 * Example JSON value:
	 * {@code "https://s7d1.scene7.com/is/image/myaemsite/hero-banner"}
	 *
	 * @return fully-qualified Scene7 URL, or {@code null}
	 */
	@JsonProperty("scene7Url")
	@Override
	public String getScene7Url() {
		return scene7Url;
	}

	/**
	 * Returns an immutable list of product identifier strings authored via the
	 * multifield dialog. Never returns {@code null} — falls back to an empty list.
	 *
	 * @return non-null, immutable list of product IDs
	 */
	@JsonProperty("productIds")
	@Override
	public List<String> getProductIds() {
		return Collections.unmodifiableList(productIds);
	}

	/**
	 * Returns the numeric banner position authored in the dialog, or {@code null}
	 * when the field has not been set.
	 *
	 * @return banner position, or {@code null}
	 */
	@JsonProperty("bannerPosition")
	@Override
	public Long getBannerPosition() {
		return bannerPosition;
	}

	@JsonProperty("title")
	@Override
	public String getTitle() {
		return title;
	}

	@JsonProperty("description")
	@Override
	public String getDescriptionText() {
		return descriptionText;
	}

	@JsonProperty("linkLabel")
	@Override
	public String getLabel() {
		return label;
	}

	@JsonProperty("link")
	@Override
	public String getLink() {
		return link;
	}

	@Override
	public String getExportedType() {
		return RESOURCE_TYPE;
	}

}
