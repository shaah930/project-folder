package com.kallista.core.models;

import java.util.List;

public interface GalleryCard {

    String getCardTitle();

    String getCardLink();

    String getVideoUrl();

    String getVideoHeading();

    String getVideoDuration();

    String getVideoAlt();

    String getImage();

    String getImageAlt();

    String getImageScene7Url();

    String getVideoPlacement();

    Boolean getVideoAutoplay();

    Boolean getVideoLoop();

    Boolean getVideoMuted();

    Boolean getVideoControls();

    Boolean getShowPlayButton();

    Boolean getDisplayControlsOnHover();

    List<GalleryCardCustomProperty> getCustomProperties();
}
