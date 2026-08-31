package dsm.foundation.core.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeaturedProductAccordionItem {
    private final String accordionTitle;
    private final String description;
    private String colorfinishestext;

    public FeaturedProductAccordionItem(String accordionTitle, String description) {
        this.accordionTitle = accordionTitle;
        this.description = description;
    }

    public String getAccordionTitle() {
        return accordionTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getColorfinishestext() {
        return colorfinishestext;
    }

    public void setColorfinishestext(String colorfinishestext) {
        this.colorfinishestext = colorfinishestext;
    }
}
