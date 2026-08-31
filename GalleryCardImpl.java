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
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.GalleryCard;
import com.kallista.core.models.GalleryCardCustomProperty;
import com.kallista.core.utils.Scene7Utils;

@Model(
    adaptables = {Resource.class, SlingHttpServletRequest.class},
    adapters = GalleryCard.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class GalleryCardImpl implements GalleryCard {

    private static final Logger LOG = LoggerFactory.getLogger(GalleryCardImpl.class);

    private static final String IMAGE_NODE = "image";

    private static final String FILE_REFERENCE = "fileReference";

    private static final String ALT = "alt";

    @SlingObject
    private Resource resource;

    @JsonIgnore
    @SlingObject
    private ResourceResolver resourceResolver;

    @JsonIgnore
    @OSGiService
    private PublishUtils publishUtils;

    @ValueMapValue(name = "cardTitle")
    private String cardTitle;

    @ValueMapValue(name = "cardLink")
    private String cardLink;

    @ValueMapValue(name = "videoUrl")
    private String videoUrl;

    @ValueMapValue(name = "videoHeading")
    private String videoHeading;

    @ValueMapValue(name = "videoDuration")
    private String videoDuration;

    @ValueMapValue(name = "videoAlt")
    private String videoAlt;

    @ValueMapValue(name = "videoPlacement")
    private String videoPlacement;

    @ValueMapValue(name = "videoAutoplay")
    private Boolean videoAutoplay;

    @ValueMapValue(name = "videoLoop")
    private Boolean videoLoop;

    @ValueMapValue(name = "videoMuted")
    private Boolean videoMuted;

    @ValueMapValue(name = "videoControls")
    private Boolean videoControls;

    @ValueMapValue(name = "showPlayButton")
    private Boolean showPlayButton;

    @ValueMapValue(name = "displayControlsOnHover")
    private Boolean displayControlsOnHover;

    @ChildResource(name = "customProperties")
    private List<GalleryCardCustomProperty> customProperties;


    private String image;

    private String imageAlt;

    private String imageScene7Url;


    @PostConstruct
    protected void init() {

        Resource imageResource = resource != null ? resource.getChild(IMAGE_NODE) : null;

        image = readStringProperty(imageResource, FILE_REFERENCE);
        imageAlt = readStringProperty(imageResource, ALT);

        if (StringUtils.isNotBlank(image)) {
            imageScene7Url = Scene7Utils.getScene7AssetPath(
                    image, resourceResolver, publishUtils);
        }

        LOG.debug("Initializing GalleryCardImpl");
        LOG.debug("cardTitle: {}", cardTitle);
        LOG.debug("cardLink: {}", cardLink);
        LOG.debug("videoUrl: {}", videoUrl);
        LOG.debug("image: {}", image);
        LOG.debug("imageAlt: {}", imageAlt);
        LOG.debug("imageScene7Url: {}", imageScene7Url);
        LOG.debug("customProperties nested multifield size: {}",
                customProperties != null ? customProperties.size() : "null");
    }


    private static String readStringProperty(Resource resource, String propertyName) {

        if (resource == null) {
            return null;
        }

        String value = resource.getValueMap().get(propertyName, String.class);

        return StringUtils.isNotBlank(value) ? value : null;
    }


    @JsonProperty("cardTitle")
    @Override
    public String getCardTitle() {
        return cardTitle;
    }


    @JsonProperty("cardLink")
    @Override
    public String getCardLink() {
        return cardLink;
    }


    @JsonProperty("videoUrl")
    @Override
    public String getVideoUrl() {
        return videoUrl;
    }


    @JsonProperty("videoHeading")
    @Override
    public String getVideoHeading() {
        return videoHeading;
    }


    @JsonProperty("videoDuration")
    @Override
    public String getVideoDuration() {
        return videoDuration;
    }


    @JsonProperty("videoAlt")
    @Override
    public String getVideoAlt() {
        return videoAlt;
    }


    @JsonProperty("image")
    @Override
    public String getImage() {
        return image;
    }


    @JsonProperty("imageAlt")
    @Override
    public String getImageAlt() {
        return imageAlt;
    }


    @JsonProperty("imageScene7Url")
    @Override
    public String getImageScene7Url() {
        return imageScene7Url;
    }


    @JsonProperty("videoPlacement")
    @Override
    public String getVideoPlacement() {
        return videoPlacement;
    }


    @JsonProperty("videoAutoplay")
    @Override
    public Boolean getVideoAutoplay() {
        return videoAutoplay;
    }


    @JsonProperty("videoLoop")
    @Override
    public Boolean getVideoLoop() {
        return videoLoop;
    }


    @JsonProperty("videoMuted")
    @Override
    public Boolean getVideoMuted() {
        return videoMuted;
    }


    @JsonProperty("videoControls")
    @Override
    public Boolean getVideoControls() {
        return videoControls;
    }


    @JsonProperty("showPlayButton")
    @Override
    public Boolean getShowPlayButton() {
        return showPlayButton;
    }


    @JsonProperty("displayControlsOnHover")
    @Override
    public Boolean getDisplayControlsOnHover() {
        return displayControlsOnHover;
    }


    @JsonProperty("customProperties")
    @Override
    public List<GalleryCardCustomProperty> getCustomProperties() {
        return customProperties != null
                ? customProperties
                : Collections.emptyList();
    }
}
