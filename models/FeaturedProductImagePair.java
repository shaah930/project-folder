package dsm.foundation.core.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeaturedProductImagePair {

    private final String swatchImage;
    private final String swatchTitle;
    private final String pairImage;

    public FeaturedProductImagePair(String swatchImage, String swatchTitle, String pairImage) {
        this.swatchImage = swatchImage;
        this.swatchTitle = swatchTitle;
        this.pairImage = pairImage;
    }

    public String getSwatchImage() {
        return swatchImage;
    }

    public String getSwatchTitle() {
        return swatchTitle;
    }

    public String getPairImage() {
        return pairImage;
    }
}
