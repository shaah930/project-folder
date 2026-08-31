package com.kallista.core.models.impl;

import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;

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

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import com.day.cq.dam.scene7.api.constants.Scene7Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.constants.KallistaConstants;
import com.kallista.core.models.BenefitCardItem;

@Model(
    adaptables = {Resource.class, SlingHttpServletRequest.class},
    adapters = BenefitCardItem.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class BenefitCardItemImpl implements BenefitCardItem {

    private static final Logger LOG = LoggerFactory.getLogger(BenefitCardItemImpl.class);

    @JsonIgnore
    @ValueMapValue
    private String imageFileReference;

    @ValueMapValue(name = "iconName")
    private String iconName;

    @ValueMapValue(name = "title")
    private String title;

    @ValueMapValue(name = "descriptionText")
    private String descriptionText;

    @ValueMapValue(name = "label")
    private String label;

    @ValueMapValue(name = "link")
    private String link;

    @ValueMapValue(name = "isModal")
    private Boolean isModal;


    @JsonIgnore
    @SlingObject
    private ResourceResolver resourceResolver;


    private String scene7Url;


    @OSGiService
    private PublishUtils publishUtils;


    @PostConstruct
    protected void init() {

        if (StringUtils.isNotBlank(imageFileReference)) {
            scene7Url = getScene7AssetPath(
                    imageFileReference,
                    resourceResolver,
                    publishUtils
            );
        }

        LOG.debug("Initializing BenefitCardItemImpl");
        LOG.debug("imageFileReference: {}", imageFileReference);
        LOG.debug("scene7Url: {}", scene7Url);
        LOG.debug("iconName: {}", iconName);
        LOG.debug("title: {}", title);
        LOG.debug("descriptionText: {}", descriptionText);
        LOG.debug("label: {}", label);
        LOG.debug("link: {}", link);
        LOG.debug("isModal: {}", isModal);
    }

    public static String getScene7AssetPath(String assetPath, ResourceResolver resourceResolver,
			PublishUtils publishUtils) {
		if (assetPath == null || assetPath.isEmpty()) {
			return assetPath;
		}
		Resource assetResource = resourceResolver.getResource(assetPath);
		Asset asset = assetResource.adaptTo(Asset.class);
		String dmAssetName = asset.getMetadataValue(Scene7Constants.PN_S7_FILE);
		if (dmAssetName == null || dmAssetName.isEmpty()) {
			return assetPath;
		}
		String[] productionAssetUrls = null;

		try {
			productionAssetUrls = publishUtils.externalizeImageDeliveryAsset(assetResource);
		} catch (RepositoryException e) {
			LOG.error("RepositoryException while fetting scene7assetPath");
		}

		String baseUrl = productionAssetUrls != null ? productionAssetUrls[0] : StringUtils.EMPTY;
		if (isVideoAsset(assetResource)) {
			return baseUrl + KallistaConstants.VIDEO_SERVER_PATH + dmAssetName;
		} else {
			return baseUrl + KallistaConstants.IMAGE_SERVER_PATH + dmAssetName;
		}
	}

	/**
	 * Returns true if the asset is a video (currently only checks for mp4 extension
	 * or video/mp4 mime type)
	 */
	public static boolean isVideoAsset(Resource assetResource) {
		if (assetResource == null)
			return false;
		Asset asset = assetResource.adaptTo(Asset.class);
		if (asset == null)
			return false;
		String mimeType = asset.getMimeType();
		if (mimeType != null && mimeType.startsWith("video/")) {
			return true;
		}
		String path = assetResource.getPath();
		if (path != null && path.toLowerCase().endsWith(".mp4")) {
			return true;
		}
		return false;
	}


    @JsonProperty("iconImage")
    @Override
    public String getScene7Url() {
        return scene7Url;
    }


    @JsonProperty("iconName")
    @Override
    public String getIconName() {
        return iconName;
    }


    @JsonProperty("title")
    @Override
    public String getTitle() {
        return title;
    }


    @JsonProperty("descriptionText")
    @Override
    public String getDescriptionText() {
        return descriptionText;
    }


    @JsonProperty("label")
    @Override
    public String getLabel() {
        return label;
    }


    @JsonProperty("link")
    @Override
    public String getLink() {
        return link;
    }


    @JsonProperty("isModal")
    @Override
    public Boolean getIsModal() {
        return isModal;
    }
}