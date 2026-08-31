package com.kallista.core.models.impl;

import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.scene7.api.constants.Scene7Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.constants.KallistaConstants;
import com.kallista.core.models.SellSheetCardItem;

@Model(
    adaptables = {
        Resource.class,
        SlingHttpServletRequest.class
    },
    adapters = SellSheetCardItem.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class SellSheetCardItemImpl implements SellSheetCardItem {

    private static final Logger LOG =
            LoggerFactory.getLogger(SellSheetCardItemImpl.class);

    
    @ChildResource(name = "headshotImage")
    private Resource headshotImage;

    @ValueMapValue(name = "headshotDesignerName")
    private String headshotDesignerName;

    @ValueMapValue(name = "headshotDescription")
    private String headshotDescription;

    @ValueMapValue(name = "headshotBackgroundColor")
    private String headshotBackgroundColor;

    @ValueMapValue(name = "headshotComponentAlignment")
    private String headshotComponentAlignment;

    @ValueMapValue(name = "sellSheetTitle")
    private String sellSheetTitle;

    @ValueMapValue(name = "sellSheetDesignerName")
    private String sellSheetDesignerName;

    @ChildResource(name = "cardImage")
    private Resource cardImage;

    @ChildResource(name = "cardLogo")
    private Resource cardLogo;

    @ValueMapValue(name = "cardEyebrow")
    private String cardEyebrow;

    @ValueMapValue(name = "cardTitle")
    private String cardTitle;

    @ValueMapValue(name = "cardDescription")
    private String cardDescription;

    @ValueMapValue(name = "cardLinkLabel")
    private String cardLinkLabel;

    @ValueMapValue(name = "cardLinkUrl")
    private String cardLinkUrl;

    @ValueMapValue(name = "ctaType")
    private String ctaType;

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private PublishUtils publishUtils;

    private String headshotImageUrl;
    private String headshotImageAlt;

    private String cardImageUrl;
    private String cardImageAlt;

    private String cardLogoUrl;
    private String cardLogoAlt;

    @PostConstruct
    private void init() {
        LOG.debug("Initializing SellSheetCardItemImpl");

        headshotImageUrl = getImageUrl(headshotImage);
        headshotImageAlt = getImageAlt(headshotImage);

        cardImageUrl = getImageUrl(cardImage);
        cardImageAlt = getImageAlt(cardImage);

        cardLogoUrl = getImageUrl(cardLogo);
        cardLogoAlt = getImageAlt(cardLogo);
    }

    private String getImageUrl(Resource imageResource) {
        if (imageResource == null) {
            return null;
        }

        String fileReference =
                imageResource.getValueMap().get(
                        "fileReference",
                        String.class);

        if (StringUtils.isBlank(fileReference)) {
            return null;
        }

        return getScene7AssetPath(
                fileReference,
                resourceResolver,
                publishUtils);
    }

    private String getImageAlt(Resource imageResource) {
        if (imageResource == null) {
            return null;
        }

        return imageResource.getValueMap().get(
                "alt",
                String.class);
    }

    public static String getScene7AssetPath(
            String assetPath,
            ResourceResolver resourceResolver,
            PublishUtils publishUtils) {

        if (StringUtils.isBlank(assetPath)) {
            return assetPath;
        }

        Resource assetResource =
                resourceResolver.getResource(assetPath);

        if (assetResource == null) {
            return assetPath;
        }

        Asset asset = assetResource.adaptTo(Asset.class);

        if (asset == null) {
            return assetPath;
        }

        String dmAssetName =
                asset.getMetadataValue(Scene7Constants.PN_S7_FILE);

        if (StringUtils.isBlank(dmAssetName)) {
            return assetPath;
        }

        String[] productionAssetUrls = null;

        try {
            productionAssetUrls =
                    publishUtils.externalizeImageDeliveryAsset(
                            assetResource);
        } catch (RepositoryException e) {
            LOG.error(
                    "RepositoryException while fetching Scene7 asset path",
                    e);
        }

        String baseUrl =
                productionAssetUrls != null
                        && productionAssetUrls.length > 0
                        ? productionAssetUrls[0]
                        : StringUtils.EMPTY;

        if (isVideoAsset(assetResource)) {
            return baseUrl
                    + KallistaConstants.VIDEO_SERVER_PATH
                    + dmAssetName;
        }

        return baseUrl
                + KallistaConstants.IMAGE_SERVER_PATH
                + dmAssetName;
    }

    public static boolean isVideoAsset(Resource assetResource) {

        if (assetResource == null) {
            return false;
        }

        Asset asset = assetResource.adaptTo(Asset.class);

        if (asset == null) {
            return false;
        }

        String mimeType = asset.getMimeType();

        if (mimeType != null && mimeType.startsWith("video/")) {
            return true;
        }

        String path = assetResource.getPath();

        return path != null
                && path.toLowerCase().endsWith(".mp4");
    }

    
    @JsonProperty("headshotImage")
    @Override
    public String getHeadshotImage() {
        return headshotImageUrl;
    }

    @JsonProperty("headshotImageAlt")
    @Override
    public String getHeadshotImageAlt() {
        return headshotImageAlt;
    }

    @JsonProperty("headshotDesignerName")
    @Override
    public String getHeadshotDesignerName() {
        return headshotDesignerName;
    }

    @JsonProperty("headshotDescription")
    @Override
    public String getHeadshotDescription() {
        return headshotDescription;
    }

    @JsonProperty("headshotBackgroundColor")
    @Override
    public String getHeadshotBackgroundColor() {
        return headshotBackgroundColor;
    }

    @JsonProperty("headshotComponentAlignment")
    @Override
    public String getHeadshotComponentAlignment() {
        return headshotComponentAlignment;
    }

    @JsonProperty("sellSheetTitle")
    @Override
    public String getSellSheetTitle() {
        return sellSheetTitle;
    }

    @JsonProperty("sellSheetDesignerName")
    @Override
    public String getSellSheetDesignerName() {
        return sellSheetDesignerName;
    }

    @JsonProperty("cardImage")
    @Override
    public String getCardImage() {
        return cardImageUrl;
    }

    @JsonProperty("cardImageAlt")
    @Override
    public String getCardImageAlt() {
        return cardImageAlt;
    }

    @JsonProperty("cardLogo")
    @Override
    public String getCardLogo() {
        return cardLogoUrl;
    }

    @JsonProperty("cardLogoAlt")
    @Override
    public String getCardLogoAlt() {
        return cardLogoAlt;
    }

    @JsonProperty("cardEyebrow")
    @Override
    public String getCardEyebrow() {
        return cardEyebrow;
    }

    @JsonProperty("cardTitle")
    @Override
    public String getCardTitle() {
        return cardTitle;
    }

    @JsonProperty("cardDescription")
    @Override
    public String getCardDescription() {
        return cardDescription;
    }

    @JsonProperty("cardLinkLabel")
    @Override
    public String getCardLinkLabel() {
        return cardLinkLabel;
    }

    @JsonProperty("cardLinkUrl")
    @Override
    public String getCardLinkUrl() {
        return cardLinkUrl;
    }

    @JsonProperty("ctaType")
    @Override
    public String getCtaType() {
        return ctaType;
    }
}