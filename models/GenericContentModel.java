package dsm.foundation.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dsm.foundation.core.utils.AssetUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * Sling Model for Generic Content Component
 * Handles multifield items with different types: Title, Description, CTA, Card
 * Supports headless/SPA via ComponentExporter
 */
@Model(
    adaptables = {SlingHttpServletRequest.class, Resource.class},
    adapters = {GenericContentModel.class, ComponentExporter.class},
    resourceType = GenericContentModel.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(
    fieldVisibility = Visibility.NONE,
    getterVisibility = Visibility.PUBLIC_ONLY,
    isGetterVisibility = Visibility.NONE
)
public class GenericContentModel implements ComponentExporter {

    private static final Logger LOG = LoggerFactory.getLogger(GenericContentModel.class);

    protected static final String RESOURCE_TYPE = "aem-dsm-foundation/components/generic-content/v1/generic-content";

    @SlingObject
    private Resource resource;

    @Inject
	PublishUtils publishUtils;

    @JsonIgnore
    private transient List<ContentItem> items;

    // Component-level settings - injected for nested object creation
    @JsonIgnore
    @ValueMapValue
    private String eyebrow;

    @JsonIgnore
    @ValueMapValue
    private String subheading;

    @JsonIgnore
    @ValueMapValue
    private String textPlacement;

    @JsonIgnore
    @ValueMapValue
    private String contentAlignment;

    @JsonIgnore
    @ValueMapValue
    private String layoutType;

    @JsonIgnore
    @ValueMapValue
    private String topSpacer;

    @JsonIgnore
    @ValueMapValue
    private String bottomSpacer;

    @JsonIgnore
    @ValueMapValue
    private String themeModifier;

    @JsonIgnore
    @ValueMapValue
    private Boolean enableHeaderNavTransparency;

    @JsonIgnore
    @ValueMapValue
    private Boolean showBottomContent;

    @JsonIgnore
    @ValueMapValue
    private String bannerMod;

    // Image settings - injected for nested object creation
    @JsonIgnore
    @ValueMapValue
    private String backgroundImageFileReference;

    @JsonIgnore
    @ValueMapValue
    private String backgroundImageAlt;

    @JsonIgnore
    @ValueMapValue
    private String imagePosition;

    @JsonIgnore
    @ValueMapValue
    private String imageOrientation;

    @JsonIgnore
    @ValueMapValue
    private Boolean hasPhotoOverlay;

    @JsonIgnore
    @ValueMapValue
    private Boolean hasImagePadding;


    @JsonIgnore
    @ValueMapValue
    private String gradientDirection;

    @JsonIgnore
    @ValueMapValue
    private Integer overlayOpacity;

    // Video settings - injected for processing
    @ChildResource
    private List<Resource> videoItems;

    // Custom fields - injected for processing
    @ChildResource
    private List<Resource> customFieldItems;

    // DSM Module identification
    @ValueMapValue
    private String dsmModule;

    @ValueMapValue
    private String dsmModuleCustomName;

    // Content type filter checkboxes
    @ValueMapValue
    private Boolean showTitles;

    @ValueMapValue
    private Boolean showDescriptions;

    @ValueMapValue
    private Boolean showCtas;

    @ValueMapValue
    private Boolean showCards;

    @ValueMapValue
    private Integer visibleCardsCount;

    @ValueMapValue
    private Boolean showImage;

    @ValueMapValue
    private Boolean showVideo;

    @ValueMapValue
    private Boolean showCustomFields;

    @ValueMapValue
    private Boolean showSettings;

    @PostConstruct
    protected void init() {
        items = new ArrayList<>();

        // Read from all four multifield paths and combine into single items list
        readItemsFromMultifield("titleItems");
        readItemsFromMultifield("descriptionItems");
        readItemsFromMultifield("ctaItems");
        readItemsFromMultifield("cardItems");
    }

    /**
     * Helper method to read items from a specific multifield path
     * Special handling for ctaItems: aggregate all into a single CTA item
     * @param multifieldName the name of the multifield node
     */
    private void readItemsFromMultifield(String multifieldName) {
        Resource multifieldResource = resource.getChild(multifieldName);
        if (multifieldResource == null) {
            return;
        }

        // Special handling for CTAs: aggregate all CTA multifield items into one
        if ("ctaItems".equals(multifieldName)) {
            List<Resource> ctaResources = new ArrayList<>();
            multifieldResource.getChildren().forEach(ctaResources::add);
            
            if (!ctaResources.isEmpty()) {
                // Create a wrapper ContentItem that will aggregate all CTAs
                ContentItem ctaContainer = new ContentItem();
                ctaContainer.type = "cta";
                ctaContainer.resource = multifieldResource; // Point to parent for aggregation
                ctaContainer.ctaResources = ctaResources; // Store all CTA resources
                items.add(ctaContainer);
            }
        } else {
            // Normal handling for other types
            StreamSupport.stream(multifieldResource.getChildren().spliterator(), false)
                .forEach(itemResource -> {
                    ContentItem item = itemResource.adaptTo(ContentItem.class);
                    if (item != null && item.getType() != null) {
                        items.add(item);
                    }
                });
        }
    }

    /**
     * Get titles as an array
     * Only returns data if showTitles checkbox is checked
     * @return List of title objects, or null if filter unchecked
     */
    @JsonProperty("titles")
    public List<Map<String, Object>> getTitles() {
        return (showTitles != null && showTitles) ? getItemsByType("title") : null;
    }

    /**
     * Get descriptions as an array
     * Only returns data if showDescriptions checkbox is checked
     * @return List of description objects, or null if filter unchecked
     */
    @JsonProperty("descriptions")
    public List<Map<String, Object>> getDescriptions() {
        return (showDescriptions != null && showDescriptions) ? getItemsByType("description") : null;
    }

    /**
     * Get CTAs as an array of CTA objects
     * @return List of CTA objects
     */
    @JsonProperty("ctas")
    public List<Map<String, Object>> getCtasList() {
        // Return null if filter is unchecked
        if (showCtas == null || !showCtas) {
            return null;
        }
        
        if (items == null || items.isEmpty()) {
            return null;
        }

        List<Map<String, Object>> ctas = new ArrayList<>();
        for (ContentItem item : items) {
            if ("cta".equals(item.getType()) && item.getCtaItems() != null) {
                // For CTAs, return the individual CTA buttons, not the wrapper
                for (CardCTA cta : item.getCtaItems()) {
                    Map<String, Object> ctaMap = new HashMap<>();
                    if (cta.getLabel() != null) ctaMap.put("label", cta.getLabel());
                    if (cta.getLink() != null) ctaMap.put("link", cta.getLink());
                    if (cta.getType() != null) ctaMap.put("type", cta.getType());
                    if (cta.getNewtab() != null) ctaMap.put("newtab", cta.getNewtab());
                    if (cta.getEnableplayicon() != null) ctaMap.put("enableplayicon", cta.getEnableplayicon());
                    ctas.add(ctaMap);
                }
            }
        }
        return ctas.isEmpty() ? null : ctas;
    }

    /**
     * Get cards as an array
     * Only returns data if showCards checkbox is checked
     * @return List of card objects, or null if filter unchecked
     */
    @JsonProperty("cards")
    public List<Map<String, Object>> getCards() {
        return (showCards != null && showCards) ? getItemsByType("card") : null;
    }

    @JsonProperty("visibleCardsCount")
    public Integer getVisibleCardsCount() {
        return visibleCardsCount;
    }

    /**
     * Helper method to get items by type
     * @param type the content type
     * @return List of items of the specified type
     */
    private List<Map<String, Object>> getItemsByType(String type) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        List<Map<String, Object>> filteredItems = new ArrayList<>();
        for (ContentItem item : items) {
            if (type.equals(item.getType())) {
                Map<String, Object> itemMap = item.toMap();
                // Remove the type field since it's redundant in grouped structure
                itemMap.remove("type");
                filteredItems.add(itemMap);
            }
        }
        return filteredItems.isEmpty() ? null : filteredItems;
    }

    /**
     * Get the list of content items as clean maps (only relevant properties)
     * @deprecated Use type-specific getters: getTitles(), getDescriptions(), getCtasList(), getCards()
     * This method is kept for HTL compatibility only and is not exported to JSON
     * @return List of Maps with only non-null, type-relevant properties
     */
    @Deprecated
    @JsonIgnore
    public List<Map<String, Object>> getItems() {
        return Collections.emptyList();
    }

    /**
     * Check if there are any content items (for backward compatibility)
     * @return true if any content type has items
     */
    public boolean hasContent() {
        return (getTitles() != null && !getTitles().isEmpty()) ||
               (getDescriptions() != null && !getDescriptions().isEmpty()) ||
               (getCtasList() != null && !getCtasList().isEmpty()) ||
               (getCards() != null && !getCards().isEmpty());
    }

    /**
     * Check if the component has any content items to render
     * Used by HTL template to conditionally render the component
     * @return true if there are items to render
     */
    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }

    // Component-level settings getters

    /**
     * Get component settings as a nested object
     * @return Settings object with all component-level settings
     */
    @JsonProperty("settings")
    public Settings getSettings() {
        // Return null if filter is unchecked
        if (showSettings == null || !showSettings) {
            return null;
        }

        // Check if any settings field has a value
        boolean hasAnySettings = eyebrow != null || subheading != null || textPlacement != null ||
                                contentAlignment != null || layoutType != null ||
                                topSpacer != null || bottomSpacer != null || themeModifier != null ||
                                enableHeaderNavTransparency != null || showBottomContent != null || bannerMod != null;
        
        if (!hasAnySettings) {
            return null; // Return null if no settings configured (won't be exported due to @JsonInclude)
        }

        return new Settings(
            eyebrow,
            subheading,
            textPlacement,
            contentAlignment,
            layoutType,
            topSpacer,
            bottomSpacer,
            themeModifier,
            enableHeaderNavTransparency,
            showBottomContent,
            bannerMod
        );
    }

    /**
     * Get video configuration as a nested object
     * @return Video object with all video settings, or null if no video configured
     */
    @JsonProperty("image")
    public Image getImage() {
        // Return null if filter is unchecked
        if (showImage == null || !showImage) {
            return null;
        }

        // Only return image object if backgroundImageFileReference is configured
        if (backgroundImageFileReference == null || backgroundImageFileReference.trim().isEmpty()) {
            return null;
        }

        try {
            String resolvedImgRef = AssetUtils.getScene7AssetPath(backgroundImageFileReference, resource, publishUtils);
            return new Image(
                resolvedImgRef,
                backgroundImageAlt,
                imagePosition,
                imageOrientation,
                hasPhotoOverlay != null ? hasPhotoOverlay : true,
                hasImagePadding != null ? hasImagePadding : false,
                gradientDirection,
                overlayOpacity
            );
        } catch (Exception e) {
            LOG.warn("Failed to resolve background image asset for fileReference '{}': {}", backgroundImageFileReference, e.getMessage());
            return null; // Prevents model JSON from breaking if DAM asset is missing or error occurs
        }
    }

    @JsonProperty("videos")
    public List<Video> getVideos() {
        // Return null if filter is unchecked
        if (showVideo == null || !showVideo) {
            return null;
        }

        // Return null if no video items configured
        if (videoItems == null || videoItems.isEmpty()) {
            return null;
        }

        List<Video> videos = new ArrayList<>();
        for (Resource videoItem : videoItems) {
            ValueMap props = videoItem.getValueMap();
            
            String videoUrl = props.get("videoUrl", String.class);
            // Skip items without a URL
            if (videoUrl == null || videoUrl.trim().isEmpty()) {
                continue;
            }

            String videoHeading = props.get("videoHeading", String.class);
            String videoDuration = props.get("videoDuration", String.class);
            String videoAlt = props.get("videoAlt", String.class);
            String videoPlacement = props.get("videoPlacement", String.class);
            Boolean videoAutoplay = props.get("videoAutoplay", Boolean.class);
            Boolean videoLoop = props.get("videoLoop", Boolean.class);
            Boolean videoMuted = props.get("videoMuted", Boolean.class);
            Boolean videoControls = props.get("videoControls", Boolean.class);
            Boolean showPlayButton = props.get("showPlayButton", Boolean.class);
            Boolean displayControlsOnHover = props.get("displayControlsOnHover", Boolean.class);

            // Get poster image if configured
            String posterImage = null;
            Resource videoPoster = videoItem.getChild("videoPoster");
            if (videoPoster != null) {
                posterImage = videoPoster.getValueMap().get("fileReference", String.class);
                posterImage = AssetUtils.getScene7AssetPath(posterImage, resource, publishUtils);
            }

            videos.add(new Video(
                videoUrl,
                videoHeading,
                videoDuration,
                videoAlt,
                posterImage,
                videoPlacement,
                videoAutoplay != null ? videoAutoplay : false,
                videoLoop != null ? videoLoop : false,
                videoMuted != null ? videoMuted : false,
                videoControls != null ? videoControls : true,
                showPlayButton != null ? showPlayButton : false,
                displayControlsOnHover != null ? displayControlsOnHover : false
            ));
        }

        return videos.isEmpty() ? null : videos;
    }

    /**
     * Check if videos are configured (for HTL usage)
     * @return true if at least one video is configured
     */
    @JsonIgnore
    public boolean hasVideos() {
        List<Video> videos = getVideos();
        return videos != null && !videos.isEmpty();
    }

    /**
     * Get the DSM Module name
     * Returns dropdown value if set (unless "custom"), otherwise returns custom name
     * @return DSM Module name, or null if neither is set
     */
    @JsonProperty("dsmModule")
    public String getDsmModule() {
        // If dropdown is "custom", use custom name field
        if ("custom".equals(dsmModule)) {
            return dsmModuleCustomName != null && !dsmModuleCustomName.trim().isEmpty() 
                ? dsmModuleCustomName 
                : null;
        }
        // Otherwise use dropdown value
        if (dsmModule != null && !dsmModule.trim().isEmpty()) {
            return dsmModule;
        }
        return null;
    }

    /**
     * Get custom fields as a list of key-value pairs
     * Only returns data if showCustomFields checkbox is checked
     * @return List of CustomField objects, or null if filter unchecked or none configured
     */
    @JsonProperty("customFields")
    public List<CustomField> getCustomFields() {
        // Return null if filter is unchecked
        if (showCustomFields == null || !showCustomFields) {
            return null;
        }
        
        if (customFieldItems == null || customFieldItems.isEmpty()) {
            return null;
        }

        List<CustomField> fields = new ArrayList<>();
        for (Resource fieldResource : customFieldItems) {
            CustomField field = fieldResource.adaptTo(CustomField.class);
            if (field != null && field.getKey() != null && !field.getKey().trim().isEmpty()) {
                fields.add(field);
            }
        }

        return fields.isEmpty() ? null : fields;
    }

    /**
     * Get a specific custom field value by key (for HTL usage)
     * @param key The field key to look up
     * @return The field value, or null if not found
     */
    @JsonIgnore
    public String getCustomField(String key) {
        List<CustomField> fields = getCustomFields();
        if (fields == null || key == null) {
            return null;
        }

        return fields.stream()
            .filter(field -> key.equals(field.getKey()))
            .map(CustomField::getValue)
            .findFirst()
            .orElse(null);
    }
    

    @Override
    public String getExportedType() {
        return resource != null ? resource.getResourceType() : "";
    }

    /**
     * Inner class representing a single CTA item within a card
     */
    @Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
    )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CardCTA {

        @ValueMapValue
        private String label;

        @ValueMapValue
        private String link;

        @ValueMapValue
        private String ctaType;

        @ValueMapValue
        private Boolean newtab;

        @ValueMapValue
        private Boolean enableplayicon;


        @JsonProperty("label")
        public String getLabel() {
            return label;
        }

        @JsonProperty("link")
        public String getLink() {
            return link;
        }

        @JsonProperty("type")
        public String getType() {
            return ctaType;
        }

        @JsonProperty("newtab")
        public Boolean getNewtab() {
            // AEM checkbox: when checked, property is set to true
            // When unchecked, property may be null or false
            if (newtab == null) {
                return false;
            }
            return newtab;
        }

        @JsonProperty("enableplayicon")
        public Boolean getEnableplayicon() {
            if (enableplayicon == null) {
                return false;
            }
            return enableplayicon;
        }

        public boolean hasContent() {
            // Temporary debug: always return true
            return true;
        }

        // For debugging
        public String toString() {
            return "CardCTA{label='" + label + "', link='" + link + "'}";
        }
    }

    /**
     * Nested class for compare list items
     * Handles label, isChecked, and showIcon properties
     */
    @Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
    )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CompareListItem {

        @ValueMapValue
        private String label;

        @ValueMapValue
        private Boolean isChecked;

        @ValueMapValue
        private Boolean showIcon;

        @JsonProperty("label")
        public String getLabel() {
            return label;
        }

        @JsonProperty("isChecked")
        public Boolean getIsChecked() {
            return isChecked != null ? isChecked : false;
        }

        @JsonProperty("showIcon")
        public Boolean getShowIcon() {
            return showIcon != null ? showIcon : false;
        }

        public boolean hasContent() {
            return label != null && !label.trim().isEmpty();
        }

        // For debugging
        public String toString() {
            return "CompareListItem{label='" + label + "', isChecked=" + isChecked + ", showIcon=" + showIcon + "}";
        }
    }

    /**
     * Inner class representing a single content item in the multifield
     * Only exports non-null properties to keep JSON lean
     */
    @Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
    )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties({"title", "description", "cta", "card"})
    public static class ContentItem {

        @Self
        private Resource resource;

        @Inject
	    PublishUtils publishUtils;

        @ValueMapValue
        private String type;

        // For aggregating multiple CTA resources
        private List<Resource> ctaResources;

        // Title type fields
        @ValueMapValue
        private String title;

        @ValueMapValue
        private String headingLevel;

        @ValueMapValue
        private String titleLink;

        @ValueMapValue
        private Boolean titleLinkNewtab;

        // Description type fields
        @ValueMapValue
        private String descriptionText;


        // Card type fields
        @ValueMapValue
        private String cardEyebrow;

        @ValueMapValue
        private String cardTitle;

        @ValueMapValue
        private String cardDescription;

        @ValueMapValue
        private String cardLinkLabel;

        @ValueMapValue
        private String cardLinkUrl;


        @ValueMapValue
        private String aspectRatio;
        
        @ValueMapValue
        private int cardRank;

        // New: Card image position (left/right)
        @ValueMapValue
        private String cardImagePosition;

        // Card image stored as sub-resource
        @ChildResource
        private Resource cardImage;

        // Card CTAs stored as multifield
        @ChildResource
        private List<Resource> cardCtas;

        // Compare List stored as multifield
        @ChildResource
        private List<Resource> compareList;

        /**
         * Get the type of content item
         * @return type (title, description, cta, card)
         */
        @JsonProperty("type")
        public String getType() {
            return type;
        }

        /**
         * Check if this is a Title type
         * Used by HTL rendering, not exported to JSON
         * @return true if type is "title"
         */
        @JsonIgnore
        public boolean isTitle() {
            return "title".equals(type);
        }

        /**
         * Check if this is a Description type
         * Used by HTL rendering, not exported to JSON
         * @return true if type is "description"
         */
        @JsonIgnore
        public boolean isDescription() {
            return "description".equals(type);
        }

        /**
         * Check if this is a CTA type
         * Used by HTL rendering, not exported to JSON
         * @return true if type is "cta"
         */
        @JsonIgnore
        public boolean isCta() {
            return "cta".equals(type);
        }

        /**
         * Check if this is a Card type
         * Used by HTL rendering, not exported to JSON
         * @return true if type is "card"
         */
        @JsonIgnore
        public boolean isCard() {
            return "card".equals(type);
        }

        @JsonIgnore
        public boolean hasItems() {
            if (type == null) return false;

            switch (type) {
                case "title":
                    return title != null && !title.trim().isEmpty();
                case "description":
                    return descriptionText != null && !descriptionText.trim().isEmpty();
                case "cta":
                    return getCtaItems() != null && !getCtaItems().isEmpty();
                case "card":
                    return (cardEyebrow != null && !cardEyebrow.trim().isEmpty()) ||
                           (cardTitle != null && !cardTitle.trim().isEmpty()) ||
                           (cardDescription != null && !cardDescription.trim().isEmpty()) ||
                           (getCardImage() != null) ||
                           (getCardCtas() != null && !getCardCtas().isEmpty()) ||
                           (getCompareList() != null && !getCompareList().isEmpty());
                default:
                    return false;
            }
        }

        // Title type getters
        @JsonProperty("title")
        public String getTitle() {
            return title;
        }

        @JsonProperty("headingLevel")
        public String getHeadingLevel() {
            return headingLevel;
        }

        @JsonProperty("titleLink")
        public String getTitleLink() {
            return titleLink;
        }

        @JsonProperty("titleLinkNewtab")
        public Boolean getTitleLinkNewtab() {
            return titleLinkNewtab != null ? titleLinkNewtab : false;
        }

        // Description type getters
        @JsonProperty("descriptionText")
        public String getDescriptionText() {
            return descriptionText;
        }

        /**
         * Get CTA items as a list of CardCTA objects
         * Aggregates all CTA resources from the multifield
         * @return List of CardCTA objects for CTA items
         */
        @JsonProperty("ctaItems")
        public List<CardCTA> getCtaItems() {
            if (!"cta".equals(type)) {
                return null;
            }

            // Use aggregated CTA resources if available
            if (ctaResources != null && !ctaResources.isEmpty()) {
                List<CardCTA> ctas = new ArrayList<>();
                for (Resource ctaResource : ctaResources) {
                    CardCTA cta = ctaResource.adaptTo(CardCTA.class);
                    if (cta != null && cta.hasContent()) {
                        ctas.add(cta);
                    }
                }
                return ctas.isEmpty() ? null : ctas;
            }

            return null;
        }

        // Card type getters
        @JsonProperty("cardEyebrow")
        public String getCardEyebrow() {
            return cardEyebrow;
        }

        @JsonProperty("cardTitle")
        public String getCardTitle() {
            return cardTitle;
        }
        
        @JsonProperty("cardRank")
        public int getCardRank() {
            return cardRank;
        }
        
        

        @JsonProperty("cardDescription")
        public String getCardDescription() {
            return cardDescription;
        }

        /**
         * Get card image file reference from sub-resource
         * @return image path from DAM
         */
        @JsonProperty("cardImage")
        public String getCardImage() {
            if (cardImage != null) {
                String fileReference = cardImage.getValueMap().get("fileReference", String.class);
                if (fileReference == null || fileReference.trim().isEmpty()) {
                    return null;
                }
                try {
                    return AssetUtils.getScene7AssetPath(fileReference, resource, publishUtils);
                } catch (Exception e) {
                    LOG.warn("Failed to resolve card image asset for fileReference '{}': {}", fileReference, e.getMessage());
                    return null;
                }
            }
            return null;
        }

        /**
         * Get card image file name from sub-resource
         * @return image file name
         */
        @JsonIgnore
        public String getCardImageFileName() {
            if (cardImage != null) {
                return cardImage.getValueMap().get("fileName", String.class);
            }
            return null;
        }

        /**
         * Get card image alt text from sub-resource
         * @return alt text for accessibility
         */
        @JsonProperty("cardImageAlt")
        public String getCardImageAlt() {
            if (cardImage != null) {
                return cardImage.getValueMap().get("alt", String.class);
            }
            return null;
        }

        @JsonProperty("cardLinkLabel")
        public String getCardLinkLabel() {
            return cardLinkLabel;
        }

        @JsonProperty("cardLinkUrl")
        public String getCardLinkUrl() {
            return cardLinkUrl;
        }


        @JsonProperty("aspectRatio")
        public String getAspectRatio() {
            return aspectRatio;
        }

        // New: Card image position getter
        @JsonProperty("cardImagePosition")
        public String getCardImagePosition() {
            return cardImagePosition;
        }

        /**
         * Get card CTAs as a list of CardCTA objects
         * @return List of CardCTA objects
         */
        @JsonProperty("cardCtas")
        public List<CardCTA> getCardCtas() {
            if (cardCtas == null || cardCtas.isEmpty()) {
                return null;
            }

            List<CardCTA> ctas = new ArrayList<>();
            for (Resource ctaResource : cardCtas) {
                CardCTA cta = ctaResource.adaptTo(CardCTA.class);
                if (cta != null && cta.hasContent()) {
                    ctas.add(cta);
                }
            }
            return ctas.isEmpty() ? null : ctas;
        }

        /**
         * Get compare list as a list of CompareListItem objects
         * @return List of CompareListItem objects
         */
        @JsonProperty("compareList")
        public List<CompareListItem> getCompareList() {
            if (compareList == null || compareList.isEmpty()) {
                return null;
            }

            List<CompareListItem> items = new ArrayList<>();
            for (Resource itemResource : compareList) {
                CompareListItem item = itemResource.adaptTo(CompareListItem.class);
                if (item != null && item.hasContent()) {
                    items.add(item);
                }
            }
            return items.isEmpty() ? null : items;
        }

        /**
         * Get a clean map with only relevant properties based on type
         * Used for lean JSON export
         * @return Map with only non-null, relevant properties
         */
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("type", type);
            
            switch (type) {
                case "title":
                    if (title != null) map.put("title", title);
                    if (headingLevel != null) map.put("headingLevel", headingLevel);
                    if (titleLink != null) map.put("titleLink", titleLink);
                    map.put("titleLinkNewtab", getTitleLinkNewtab()); // Always include with default
                    break;
                case "description":
                    if (descriptionText != null) map.put("descriptionText", descriptionText);
                    break;
                case "cta":
                    if (getCtaItems() != null) {
                        List<Map<String, Object>> ctaMaps = new ArrayList<>();
                        for (CardCTA cta : getCtaItems()) {
                            Map<String, Object> ctaMap = new HashMap<>();
                            if (cta.getLabel() != null) ctaMap.put("label", cta.getLabel());
                            if (cta.getLink() != null) ctaMap.put("link", cta.getLink());
                            if (cta.getType() != null) ctaMap.put("type", cta.getType());
                            if (cta.getNewtab() != null) ctaMap.put("newtab", cta.getNewtab());
                            if (cta.getEnableplayicon() != null) ctaMap.put("enableplayicon", cta.getEnableplayicon());
                            ctaMaps.add(ctaMap);
                        }
                        map.put("ctaItems", ctaMaps);
                    }
                    break;
                case "card":
                    if (cardEyebrow != null) map.put("cardEyebrow", cardEyebrow);
                    if (cardTitle != null) map.put("cardTitle", cardTitle);
                    if (cardRank != 0) map.put("cardRank", cardRank);
                    if (cardDescription != null) map.put("cardDescription", cardDescription);
                    if (getCardImage() != null) map.put("cardImage", getCardImage());
                    if (getCardImageAlt() != null) map.put("cardImageAlt", getCardImageAlt());
                    if (cardLinkLabel != null) map.put("cardLinkLabel", cardLinkLabel);
                    if (cardLinkUrl != null) map.put("cardLinkUrl", cardLinkUrl);
                    if (aspectRatio != null) map.put("aspectRatio", aspectRatio);
                    if (cardImagePosition != null) map.put("cardImagePosition", cardImagePosition);
                    if (getCardCtas() != null) {
                        List<Map<String, Object>> ctaMaps = new ArrayList<>();
                        for (CardCTA cta : getCardCtas()) {
                            Map<String, Object> ctaMap = new HashMap<>();
                            if (cta.getLabel() != null) ctaMap.put("label", cta.getLabel());
                            if (cta.getLink() != null) ctaMap.put("link", cta.getLink());
                            if (cta.getType() != null) ctaMap.put("type", cta.getType());
                            if (cta.getNewtab() != null) ctaMap.put("newtab", cta.getNewtab());
                            if (cta.getEnableplayicon() != null) ctaMap.put("enableplayicon", cta.getEnableplayicon());
                            ctaMaps.add(ctaMap);
                        }
                        map.put("cardCtas", ctaMaps);
                    }
                    if (getCompareList() != null) {
                        List<Map<String, Object>> compareMaps = new ArrayList<>();
                        for (CompareListItem item : getCompareList()) {
                            Map<String, Object> itemMap = new HashMap<>();
                            if (item.getLabel() != null) itemMap.put("label", item.getLabel());
                            itemMap.put("isChecked", item.getIsChecked());
                            itemMap.put("showIcon", item.getShowIcon());
                            compareMaps.add(itemMap);
                        }
                        map.put("compareList", compareMaps);
                    }
                    break;
            }
            
            return map;
        }
    }

    /**
     * Nested class for component settings
     * Groups all settings-related fields for cleaner JSON export
     */
    public static class Settings {
        private String eyebrow;
        private String subheading;
        private String textPlacement;
        private String contentAlignment;
        private String layoutType;

        private String topSpacer;
        private String bottomSpacer;
        private String themeModifier;
        private Boolean enableHeaderNavTransparency;
        private Boolean showBottomContent;
        private String bannerMod;

        public Settings(String eyebrow, String subheading, String textPlacement,
                       String contentAlignment, String layoutType,
                       String topSpacer, String bottomSpacer, String themeModifier,
                       Boolean enableHeaderNavTransparency, Boolean showBottomContent,
                       String bannerMod) {
            this.eyebrow = eyebrow;
            this.subheading = subheading;
            this.textPlacement = textPlacement;
            this.contentAlignment = contentAlignment;
            this.layoutType = layoutType;

            this.topSpacer = topSpacer;
            this.bottomSpacer = bottomSpacer;
            this.themeModifier = themeModifier;
            this.enableHeaderNavTransparency = enableHeaderNavTransparency;
            this.showBottomContent = showBottomContent;
            this.bannerMod = bannerMod;
        }

        @JsonProperty("eyebrow")
        public String getEyebrow() { return eyebrow; }

        @JsonProperty("subheading")
        public String getSubheading() { return subheading; }

        @JsonProperty("textPlacement")
        public String getTextPlacement() { return textPlacement; }

        @JsonProperty("contentAlignment")
        public String getContentAlignment() { return contentAlignment; }

        @JsonProperty("layoutType")
        public String getLayoutType() { return layoutType; }

        @JsonProperty("topSpacer")
        public String getTopSpacer() { return topSpacer; }

        @JsonProperty("bottomSpacer")
        public String getBottomSpacer() { return bottomSpacer; }

        @JsonProperty("themeModifier")
        public String getThemeModifier() { return themeModifier; }

        @JsonProperty("enableHeaderNavTransparency")
        public Boolean getEnableHeaderNavTransparency() { return enableHeaderNavTransparency; }

        @JsonProperty("showBottomContent")
        public Boolean getShowBottomContent() { return showBottomContent != null ? showBottomContent : false; }

        @JsonProperty("bannerMod")
        public String getBannerMod() { return bannerMod; }
    }

    /**
     * Nested class for video settings
     * Groups all video-related fields for cleaner JSON export
     */
    public static class Video {
        private String url;
        private String heading;
        private String duration;
        private String alt;
        private String poster;
        private String placement;
        private Boolean autoplay;
        private Boolean loop;
        private Boolean muted;
        private Boolean controls;
        private Boolean showPlayButton;
        private Boolean displayControlsOnHover;

        public Video(String url, String heading, String duration, String alt, String poster,
                    String placement, Boolean autoplay, Boolean loop, Boolean muted, Boolean controls,
                    Boolean showPlayButton, Boolean displayControlsOnHover) {
            this.url = url;
            this.heading = heading;
            this.duration = duration;
            this.alt = alt;
            this.poster = poster;
            this.placement = placement;
            this.autoplay = autoplay;
            this.loop = loop;
            this.muted = muted;
            this.controls = controls;
            this.showPlayButton = showPlayButton;
            this.displayControlsOnHover = displayControlsOnHover;
        }

        @JsonProperty("url")
        public String getUrl() { return url; }

        @JsonProperty("heading")
        public String getHeading() { return heading; }

        @JsonProperty("duration")
        public String getDuration() { return duration; }

        @JsonProperty("alt")
        public String getAlt() { return alt; }

        @JsonProperty("poster")
        public String getPoster() { return poster;}

        @JsonProperty("placement")
        public String getPlacement() { return placement; }

        @JsonProperty("autoplay")
        public Boolean getAutoplay() { return autoplay; }

        @JsonProperty("loop")
        public Boolean getLoop() { return loop; }

        @JsonProperty("muted")
        public Boolean getMuted() { return muted; }

        @JsonProperty("controls")
        public Boolean getControls() { return controls; }

        @JsonProperty("showPlayButton")
        public Boolean getShowPlayButton() { return showPlayButton; }

        @JsonProperty("displayControlsOnHover")
        public Boolean getDisplayControlsOnHover() { return displayControlsOnHover; }
    }

    /**
     * Nested class for image settings
     * Groups all image-related fields for cleaner JSON export
     */
    public static class Image {
        private String url;
        private String alt;
        private String position;
        private String orientation;
        private Boolean hasPhotoOverlay;
        private Boolean hasImagePadding;
        private String gradientDirection;
        private Integer overlayOpacity;

        public Image(String url, String alt, String position, String orientation,
                    Boolean hasPhotoOverlay,Boolean hasImagePadding , String gradientDirection, Integer overlayOpacity) {
            this.url = url;
            this.alt = alt;
            this.position = position;
            this.orientation = orientation;
            this.hasPhotoOverlay = hasPhotoOverlay;
            this.hasImagePadding = hasImagePadding;
            this.gradientDirection = gradientDirection;
            this.overlayOpacity = overlayOpacity;
        }

        @JsonProperty("url")
        public String getUrl() { return url; }

        @JsonProperty("alt")
        public String getAlt() { return alt; }

        @JsonProperty("position")
        public String getPosition() { return position; }

        @JsonProperty("orientation")
        public String getOrientation() { return orientation; }

        @JsonProperty("hasPhotoOverlay")
        public Boolean getHasPhotoOverlay() { return hasPhotoOverlay; }

        @JsonProperty("hasImagePadding")
        public Boolean getHasImagePadding() { return hasImagePadding; }

        @JsonProperty("gradientDirection")
        public String getGradientDirection() { return gradientDirection; }

        @JsonProperty("overlayOpacity")
        public Integer getOverlayOpacity() { return overlayOpacity; }
    }

    /**
     * Nested class for custom key-value fields
     * Allows flexible ad-hoc content fields like author, date, category, etc.
     */
    @Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
    )
    public static class CustomField {
        @ValueMapValue
        private String key;

        @ValueMapValue
        private String value;

        @JsonProperty("key")
        public String getKey() {
            return key;
        }

        @JsonProperty("value")
        public String getValue() {
            return value;
        }
    }
}
