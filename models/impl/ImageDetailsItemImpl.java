package com.kallista.core.models.impl;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kallista.core.models.ImageDetailsItem;

import dsm.foundation.core.utils.AssetUtils;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       adapters = ImageDetailsItem.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageDetailsItemImpl implements ImageDetailsItem {

    @Inject
    PublishUtils publishUtils;

    @SlingObject
    private Resource resource;

    @ValueMapValue(name = "imageTitle")
    private String imageTitle;

    @ValueMapValue(name = "imageAltText")
    private String imageAltText;

    @ValueMapValue(name = "imageCTALabel")
    private String imageCTALabel;

    @ValueMapValue(name = "imageCTALink")
    private String imageCTALink;

    @ValueMapValue(name = "imageCTANewTab")
    private boolean imageCTANewTab;

    @ValueMapValue(name = "fileReference")
    private String image;

    @ValueMapValue(name = "alignmentType")
    private String alignmentType;

    @JsonProperty("imageTitle")
    @Override
    public String getImageTitle() {
        return imageTitle;
    }

    @JsonProperty("imageAltText")
    @Override
    public String getImageAltText() {
        return imageAltText;
    }

    @JsonProperty("imageCTALabel")
    @Override
    public String getImageCTALabel() {
        return imageCTALabel;
    }

    @JsonProperty("imageCTALink")
    @Override
    public String getImageCTALink() {
        return imageCTALink;
    }

    @JsonProperty("imageCTANewTab")
    @Override
    public boolean isImageCTANewTab() {
        return imageCTANewTab;
    }

    @JsonProperty("image")
    @Override
    public String getImage() {
        return AssetUtils.getScene7AssetPath(image, resource, publishUtils);
    }

    @JsonProperty("alignmentType")
    @Override
    public String getAlignmentType() {
        return alignmentType;
    }
}
