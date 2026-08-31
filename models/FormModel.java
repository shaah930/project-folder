/*
 *  Copyright 2024 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package dsm.foundation.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;


import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Sling Model for Form Component
 * Handles form fields and custom fields
 * Supports headless/SPA via ComponentExporter
 */
@Model(
    adaptables = {SlingHttpServletRequest.class, Resource.class},
    adapters = {FormModel.class, ComponentExporter.class},
    resourceType = FormModel.RESOURCE_TYPE,
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
public class FormModel implements ComponentExporter {

    protected static final String RESOURCE_TYPE = "aem-dsm-foundation/components/forms";

    @SlingObject
    private Resource resource;

    // Component-level settings - injected for nested object creation
    @JsonIgnore
    @ValueMapValue
    private String description;

    @JsonIgnore
    @ValueMapValue
    private String dsmModuleType;

    @JsonIgnore
    @ValueMapValue
    private String title;

    @JsonIgnore
    @ValueMapValue
    private String headingLevel;

    @JsonIgnore
    @ValueMapValue
    private String titleLink;

    @JsonIgnore
    @ValueMapValue
    private Boolean titleOpenInNewTab;

    @ValueMapValue
    private Boolean enableAnalytics;

    @JsonIgnore
    @ValueMapValue
    private String analyticsFormId;

    @JsonIgnore
    @ValueMapValue
    private String analyticsFormName;

    @JsonIgnore
    @ValueMapValue
    private String analyticsPlacement;

    // Content type filter checkboxes
    @ValueMapValue
    private Boolean enableFormField;

    @ValueMapValue
    private Boolean enableCustomFields;

    @ValueMapValue
    private Boolean enableCards;

    @ValueMapValue
    private Boolean enableDropdownFields;

    @ValueMapValue
    private Boolean enablePaginatedForm;

    @ValueMapValue
    private Boolean isProductPage;

    // Paginated Form properties
    @JsonIgnore
    @ValueMapValue
    private String paginatedStep;

    @JsonIgnore
    @ValueMapValue
    private String promoBannerLink;

    @JsonIgnore
    @ValueMapValue
    private String referAFriendBannerLink;

    @JsonIgnore
    @ValueMapValue
    private String thankYouBannerLink;

    @JsonIgnore
    @ValueMapValue
    private String businessUnitOverride;

    @JsonIgnore
    @ValueMapValue
    private String paginatedPromoBannerLink;

    @JsonIgnore
    @ValueMapValue
    private String paginatedTitle;

    @JsonIgnore
    @ValueMapValue
    private String paginatedHeadingLevel;

    @JsonIgnore
    @ValueMapValue
    private String paginatedDescription;

    @ValueMapValue
    private String businessGroup;

    // Child resources for composite multifield data
    @ChildResource
    private List<Resource> fields;

    @ChildResource
    private List<Resource> customFieldItems;

    @ChildResource(name = "customFields")
    private List<Resource> customFields;

    @ChildResource
    private List<Resource> analyticsMetaItems;

    @ChildResource
    private List<Resource> cardItems;

    @ChildResource(name = "cards")
    private List<Resource> cards;

    @ChildResource
    private List<Resource> dropdownFieldItems;

    @ChildResource
    private List<Resource> paginatedSections;

    @ChildResource
    private List<Resource> paginatedFields;

    @ChildResource
    private List<Resource> paginatedDropdownFields;

    @ChildResource
    private List<Resource> paginatedCustomFields;

    @ChildResource
    private List<Resource> paginatedCards;

    /**
     * Gets the description text
     * @return the description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description != null && !description.trim().isEmpty() ? description : null;
    }

    /**
     * Gets the business group
     * @return the business group
     */
    @JsonProperty("businessGroup")
    public String getBusinessGroup() {
        return businessGroup != null && !businessGroup.trim().isEmpty() ? businessGroup : null;
    }

    /**
     * Gets the form type
     * @return the form type
     */
    @JsonProperty("dsmModuleType")
    public String getDsmModuleType() {
        return dsmModuleType != null && !dsmModuleType.trim().isEmpty() ? dsmModuleType : null;
    }

    /**
     * Gets whether content section (title and description) is enabled
     * Content is always enabled in this component
     * @return always true since content is always available
     */
    public boolean isEnableContent() {
        return true;
    }

    /**
     * Gets whether title section is enabled (legacy compatibility)
     * @return always true since content is always available
     */
    public boolean isEnableTitle() {
        return true;
    }

    /**
     * Gets whether description section is enabled (legacy compatibility)
     * @return always true since content is always available
     */
    public boolean isEnableDescription() {
        return true;
    }

    /**
     * Gets whether form field section is enabled
     * @return true if form field section is enabled
     */
    @JsonProperty("enableFormField")
    public boolean isEnableFormField() {
        return enableFormField != null && enableFormField;
    }



    /**
     * Gets whether custom fields section is enabled
     * @return true if custom fields section is enabled
     */
    @JsonProperty("enableCustomFields")
    public boolean isEnableCustomFields() {
        return enableCustomFields != null && enableCustomFields;
    }

    /**
     * Gets whether cards section is enabled
     * @return true if cards section is enabled
     */
    @JsonProperty("enableCards")
    public boolean isEnableCards() {
        return enableCards != null && enableCards;
    }

    /**
     * Gets whether dropdown fields section is enabled
     * @return true if dropdown fields section is enabled
     */
    @JsonProperty("enableDropdownFields")
    public boolean isEnableDropdownFields() {
        return enableDropdownFields != null && enableDropdownFields;
    }

    /**
     * Gets whether paginated form is enabled
     * @return true if paginated form is enabled
     */
    @JsonProperty("enablePaginatedForm")
    public boolean isEnablePaginatedForm() {
        return enablePaginatedForm != null && enablePaginatedForm;
    }

    @JsonProperty("isProductPage")
    public boolean isProductPage() {
        return isProductPage != null && isProductPage;
    }

    /**
     * Gets the paginated step
     * @return the paginated step
     */
    @JsonProperty("paginatedStep")
    public String getPaginatedStep() {
        return paginatedStep;
    }

    /**
     * Gets the promo banner link
     * @return the promo banner link
     */
    @JsonProperty("promoBannerLink")
    public String getPromoBannerLink() {
        return promoBannerLink;
    }

    /**
     * Gets the refer a friend banner link
     * @return the refer a friend banner link
     */
    @JsonProperty("referAFriendBannerLink")
    public String getReferAFriendBannerLink() {
        return referAFriendBannerLink;
    }

    /**
     * Gets the thank you banner link
     * @return the thank you banner link
     */
    @JsonProperty("thankYouBannerLink")
    public String getThankYouBannerLink() {
        return thankYouBannerLink;
    }

    /**
     * Gets the authored business unit override
     * @return the business unit override
     */
    @JsonProperty("businessUnitOverride")
    public String getBusinessUnitOverride() {
        return businessUnitOverride != null && !businessUnitOverride.trim().isEmpty()
            ? businessUnitOverride.trim()
            : null;
    }

    /**
     * Gets the paginated promo banner link
     * @return the paginated promo banner link
     */
    @JsonProperty("paginatedPromoBannerLink")
    public String getPaginatedPromoBannerLink() {
        return paginatedPromoBannerLink;
    }

    /**
     * Gets the paginated title
     * @return the paginated title
     */
    @JsonProperty("paginatedTitle")
    public String getPaginatedTitle() {
        return paginatedTitle != null && !paginatedTitle.trim().isEmpty() ? paginatedTitle : null;
    }

    /**
     * Gets the paginated heading level
     * @return the paginated heading level
     */
    @JsonProperty("paginatedHeadingLevel")
    public String getPaginatedHeadingLevel() {
        return paginatedHeadingLevel != null && !paginatedHeadingLevel.trim().isEmpty() ? paginatedHeadingLevel : "h2";
    }

    /**
     * Gets the paginated description
     * @return the paginated description
     */
    @JsonProperty("paginatedDescription")
    public String getPaginatedDescription() {
        return paginatedDescription != null && !paginatedDescription.trim().isEmpty() ? paginatedDescription : null;
    }

    /**
     * Gets the title text
     * @return the title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title != null && !title.trim().isEmpty() ? title : null;
    }

    /**
     * Gets the heading level
     * @return the heading level
     */
    @JsonProperty("headingLevel")
    public String getHeadingLevel() {
        return headingLevel != null && !headingLevel.trim().isEmpty() ? headingLevel : "h2";
    }

    /**
     * Gets the title link
     * @return the title link
     */
    @JsonProperty("titleLink")
    public String getTitleLink() {
        return titleLink;
    }

    /**
     * Gets whether title should open in new tab
     * @return true if title should open in new tab
     */
    @JsonProperty("titleOpenInNewTab")
    public boolean isTitleOpenInNewTab() {
        return titleOpenInNewTab != null && titleOpenInNewTab;
    }

    @JsonProperty("enableAnalytics")
    public boolean isEnableAnalytics() {
        return enableAnalytics == null || enableAnalytics;
    }

    @JsonProperty("analyticsFormId")
    public String getAnalyticsFormId() {
        return analyticsFormId != null && !analyticsFormId.trim().isEmpty() ? analyticsFormId : null;
    }

    @JsonProperty("analyticsFormName")
    public String getAnalyticsFormName() {
        return analyticsFormName != null && !analyticsFormName.trim().isEmpty() ? analyticsFormName : null;
    }

    @JsonProperty("analyticsPlacement")
    public String getAnalyticsPlacement() {
        return analyticsPlacement != null && !analyticsPlacement.trim().isEmpty() ? analyticsPlacement : null;
    }

    @JsonProperty("analyticsMetaItems")
    public List<CustomField> getAnalyticsMetaItems() {
        if (!isEnableAnalytics()) {
            return null;
        }

        if (analyticsMetaItems == null || analyticsMetaItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<CustomField> fieldsList = new ArrayList<>();
        for (Resource fieldResource : analyticsMetaItems) {
            try {
                CustomField field = fieldResource.adaptTo(CustomField.class);
                if (field != null && field.getKey() != null && !field.getKey().trim().isEmpty()) {
                    fieldsList.add(field);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return fieldsList;
    }

    /**
     * Gets the list of form fields
     * Only returns data if enableFormField checkbox is checked
     * @return List of FormField objects, or null if filter unchecked or none configured
     */
    @JsonProperty("fields")
    public List<FormField> getFields() {
        try {
            // Return null if filter is unchecked
            if (enableFormField == null || !enableFormField) {
                return null;
            }
            
            List<FormField> fieldsList = new ArrayList<>();
            
            // Try composite multifield first (current structure)
            if (fields != null && !fields.isEmpty()) {
                for (Resource fieldResource : fields) {
                    try {
                        FormField field = fieldResource.adaptTo(FormField.class);
                        if (field != null && field.getFieldLabel() != null && !field.getFieldLabel().trim().isEmpty()) {
                            fieldsList.add(field);
                        }
                    } catch (Exception e) {
                        // Skip this field if adaptation fails
                        continue;
                    }
                }
            }
            
            // Fallback to array-based data (previous structure)
            if (fieldsList.isEmpty() && resource != null) {
                ValueMap props = resource.getValueMap();
                String[] fieldLabels = props.get("fieldLabels", String[].class);
                String[] fieldInputTypes = props.get("fieldInputTypes", String[].class);
                
                // Additional fallback for legacy property names
                if (fieldLabels == null || fieldInputTypes == null) {
                    fieldLabels = props.get("fieldLabel", String[].class);
                    fieldInputTypes = props.get("inputType", String[].class);
                }
                
                if (fieldLabels != null && fieldInputTypes != null) {
                    int maxLength = Math.min(fieldLabels.length, fieldInputTypes.length);
                    for (int i = 0; i < maxLength; i++) {
                        if (fieldLabels[i] != null && !fieldLabels[i].trim().isEmpty()) {
                            String inputType = fieldInputTypes.length > i ? fieldInputTypes[i] : "textfield";
                            FormField field = new FormField(fieldLabels[i], inputType);
                            fieldsList.add(field);
                        }
                    }
                }
            }

            return fieldsList.isEmpty() ? null : fieldsList;
        } catch (Exception e) {
            // Return null if any error occurs
            return null;
        }
    }











    /**
     * Get custom fields as a list of key-value pairs
     * Only returns data if enableCustomFields checkbox is checked
     * @return List of CustomField objects, or null if filter unchecked or none configured
     */
    @JsonProperty("customFields")
    public List<CustomField> getCustomFields() {
        try {
            // Return null if filter is unchecked
            if (enableCustomFields == null || !enableCustomFields) {
                return null;
            }
            
            List<CustomField> fieldsList = new ArrayList<>();
            
            // Try composite multifield first (current structure)
            List<Resource> fieldsToProcess = null;
            if (customFieldItems != null && !customFieldItems.isEmpty()) {
                fieldsToProcess = customFieldItems;
            } else if (customFields != null && !customFields.isEmpty()) {
                fieldsToProcess = customFields;
            }
            
            if (fieldsToProcess != null) {
                for (Resource fieldResource : fieldsToProcess) {
                    try {
                        CustomField field = fieldResource.adaptTo(CustomField.class);
                        if (field != null && field.getKey() != null && !field.getKey().trim().isEmpty()) {
                            fieldsList.add(field);
                        }
                    } catch (Exception e) {
                        // Skip this field if adaptation fails
                        continue;
                    }
                }
            }
            
            // Enhanced fallback for legacy data (try multiple property name patterns)
            if (fieldsList.isEmpty() && resource != null) {
                ValueMap props = resource.getValueMap();
                
                // Try all possible property name patterns for previously saved data
                String[] customFieldKeys = null;
                String[] customFieldValues = null;
                
                // Pattern 1: Standard array properties
                customFieldKeys = props.get("customFieldKeys", String[].class);
                customFieldValues = props.get("customFieldValues", String[].class);
                
                // Pattern 2: Singular properties  
                if (customFieldKeys == null || customFieldValues == null) {
                    customFieldKeys = props.get("customFieldKey", String[].class);
                    customFieldValues = props.get("customFieldValue", String[].class);
                }
                
                // Pattern 3: Simple key/value properties
                if (customFieldKeys == null || customFieldValues == null) {
                    customFieldKeys = props.get("key", String[].class);
                    customFieldValues = props.get("value", String[].class);
                }
                
                // Pattern 4: Check for single values and convert to arrays
                if (customFieldKeys == null || customFieldValues == null) {
                    String singleKey = props.get("key", String.class);
                    String singleValue = props.get("value", String.class);
                    if (singleKey != null && !singleKey.trim().isEmpty()) {
                        customFieldKeys = new String[]{singleKey};
                        customFieldValues = new String[]{singleValue != null ? singleValue : ""};
                    }
                }
                
                // Process found data
                if (customFieldKeys != null && customFieldValues != null && customFieldKeys.length > 0) {
                    int maxLength = Math.min(customFieldKeys.length, customFieldValues.length);
                    for (int i = 0; i < maxLength; i++) {
                        if (customFieldKeys[i] != null && !customFieldKeys[i].trim().isEmpty()) {
                            String value = customFieldValues.length > i ? customFieldValues[i] : "";
                            CustomField field = new CustomField(customFieldKeys[i], value != null ? value : "");
                            fieldsList.add(field);
                        }
                    }
                }
            }

            return fieldsList.isEmpty() ? null : fieldsList;
        } catch (Exception e) {
            // Return null if any error occurs
            return null;
        }
    }



    /**
     * Gets the configured cards for the component.
     * Returns null if enableCards is unchecked to exclude from JSON export.
     * @return List of Card objects, or null if filter unchecked or none configured
     */
    @JsonProperty("cards")
    public List<Card> getCards() {
        try {
            // Return null if filter is unchecked
            if (!isEnableCards()) {
                return null;
            }
            
            List<Card> cardsList = new ArrayList<>();
            
            // Try composite multifield first (current structure)
            List<Resource> cardsToProcess = null;
            if (cardItems != null && !cardItems.isEmpty()) {
                cardsToProcess = cardItems;
            } else if (cards != null && !cards.isEmpty()) {
                cardsToProcess = cards;
            }
            
            if (cardsToProcess != null) {
                for (Resource cardResource : cardsToProcess) {
                    if (cardResource != null) {
                        try {
                            Card card = cardResource.adaptTo(Card.class);
                            if (card != null) {
                                // Add card even if title is empty, let the Card class handle validation
                                cardsList.add(card);
                            }
                        } catch (Exception e) {
                            // Skip this card if adaptation fails
                            continue;
                        }
                    }
                }
            }
            
            // Enhanced fallback for legacy card data (try multiple property name patterns)
            if (cardsList.isEmpty() && resource != null) {
                ValueMap props = resource.getValueMap();
                
                // Try all possible property name patterns for previously saved card data
                String[] cardTitles = null;
                String[] cardDescriptions = null;
                String[] cardImages = null;
                
                // Pattern 1: Standard array properties
                cardTitles = props.get("cardTitles", String[].class);
                cardDescriptions = props.get("cardDescriptions", String[].class);
                cardImages = props.get("cardImages", String[].class);
                
                // Pattern 2: Singular properties
                if (cardTitles == null) {
                    cardTitles = props.get("cardTitle", String[].class);
                    cardDescriptions = props.get("cardDescription", String[].class);
                    cardImages = props.get("cardImage", String[].class);
                }
                
                // Pattern 3: Check for single values and convert to arrays
                if (cardTitles == null) {
                    String singleTitle = props.get("cardTitle", String.class);
                    String singleDescription = props.get("cardDescription", String.class);
                    String singleImage = props.get("cardImage", String.class);
                    if (singleTitle != null && !singleTitle.trim().isEmpty()) {
                        cardTitles = new String[]{singleTitle};
                        cardDescriptions = new String[]{singleDescription != null ? singleDescription : ""};
                        cardImages = new String[]{singleImage != null ? singleImage : ""};
                    }
                }
                
                // Process found card data
                if (cardTitles != null && cardTitles.length > 0) {
                    for (int i = 0; i < cardTitles.length; i++) {
                        if (cardTitles[i] != null && !cardTitles[i].trim().isEmpty()) {
                            String description = (cardDescriptions != null && cardDescriptions.length > i) ? cardDescriptions[i] : "";
                            String image = (cardImages != null && cardImages.length > i) ? cardImages[i] : "";
                            Card card = new Card(cardTitles[i], description, image);
                            cardsList.add(card);
                        }
                    }
                }
            }

            return cardsList.isEmpty() ? null : cardsList;
        } catch (Exception e) {
            // Return null if any error occurs
            return null;
        }
    }



    @Override
    public String getExportedType() {
        return resource != null ? resource.getResourceType() : "";
    }

    /**
     * Gets the component ID for JSON export
     * @return the component ID
     */
    @JsonProperty("id")
    public String getId() {
        return resource != null ? resource.getPath() : null;
    }

    /**
     * Gets the component configuration status for JSON export
     * Always returns a value to ensure component appears in JSON
     * @return configuration object with current settings
     */
    @JsonProperty("componentStatus")
    public String getComponentStatus() {
        return "configured"; // Always return something so component appears in JSON
    }



    public boolean hasCustomFieldsData() {
        List<CustomField> data = getCustomFields();
        return data != null && !data.isEmpty();
    }










    // Inner model classes for multifield items

    /**
     * Form Field data object
     */
    @Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
    )
    public static class FormField {
        @ValueMapValue
        private String inputType;
        
        @ValueMapValue
        private String fieldLabel;
        
        // Default constructor for Sling Model adaptation
        public FormField() {}
        
        // Constructor for manual creation (legacy array conversion)
        public FormField(String fieldLabel, String inputType) {
            this.fieldLabel = fieldLabel;
            this.inputType = inputType;
        }

        @JsonProperty("inputType")
        public String getInputType() {
            return inputType;
        }

        @JsonProperty("fieldLabel")
        public String getFieldLabel() {
            return fieldLabel;
        }
    }

    /**
     * Gets the dropdown field items
     * @return list of dropdown field items
     */
    @JsonProperty("dropdownFieldItems")
    public List<DropdownField> getDropdownFieldItems() {
        if (dropdownFieldItems == null || dropdownFieldItems.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<DropdownField> dropdownList = new ArrayList<>();
        for (Resource dropdownResource : dropdownFieldItems) {
            try {
                DropdownField dropdown = dropdownResource.adaptTo(DropdownField.class);
                if (dropdown != null && dropdown.getLabel() != null && !dropdown.getLabel().trim().isEmpty()) {
                    dropdownList.add(dropdown);
                }
            } catch (Exception e) {
                // Skip this dropdown if adaptation fails
                continue;
            }
        }
        return dropdownList;
    }

    /**
     * Gets the paginated form fields
     * @return list of paginated form fields
     */
    @JsonProperty("paginatedFields")
    public List<FormField> getPaginatedFields() {
        if (paginatedFields == null || paginatedFields.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<FormField> fieldsList = new ArrayList<>();
        for (Resource fieldResource : paginatedFields) {
            try {
                FormField field = fieldResource.adaptTo(FormField.class);
                if (field != null) {
                    fieldsList.add(field);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return fieldsList;
    }

    /**
     * Gets the paginated dropdown fields
     * @return list of paginated dropdown fields
     */
    @JsonProperty("paginatedDropdownFields")
    public List<DropdownField> getPaginatedDropdownFields() {
        if (paginatedDropdownFields == null || paginatedDropdownFields.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<DropdownField> dropdownList = new ArrayList<>();
        for (Resource dropdownResource : paginatedDropdownFields) {
            try {
                DropdownField dropdown = dropdownResource.adaptTo(DropdownField.class);
                if (dropdown != null && dropdown.getLabel() != null && !dropdown.getLabel().trim().isEmpty()) {
                    dropdownList.add(dropdown);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return dropdownList;
    }

    /**
     * Gets the paginated custom fields
     * @return list of paginated custom fields
     */
    @JsonProperty("paginatedCustomFields")
    public List<CustomField> getPaginatedCustomFields() {
        if (paginatedCustomFields == null || paginatedCustomFields.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<CustomField> fieldsList = new ArrayList<>();
        for (Resource fieldResource : paginatedCustomFields) {
            try {
                CustomField field = fieldResource.adaptTo(CustomField.class);
                if (field != null && field.getKey() != null && !field.getKey().trim().isEmpty()) {
                    fieldsList.add(field);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return fieldsList;
    }

    /**
     * Gets the paginated cards
     * @return list of paginated cards
     */
    @JsonProperty("paginatedCards")
    public List<Card> getPaginatedCards() {
        if (paginatedCards == null || paginatedCards.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Card> cardsList = new ArrayList<>();
        for (Resource cardResource : paginatedCards) {
            try {
                Card card = cardResource.adaptTo(Card.class);
                if (card != null) {
                    cardsList.add(card);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return cardsList;
    }

    /**
     * Gets the paginated sections (grouped structure)
     * @return list of paginated sections containing all fields
     */
    @JsonProperty("paginatedSections")
    public List<PaginatedSection> getPaginatedSections() {
        if (paginatedSections == null || paginatedSections.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<PaginatedSection> sectionsList = new ArrayList<>();
        for (Resource sectionResource : paginatedSections) {
            try {
                PaginatedSection section = sectionResource.adaptTo(PaginatedSection.class);
                if (section != null) {
                    sectionsList.add(section);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return sectionsList;
    }

    /**
     * Paginated Section data object containing all paginated fields
     */
    @Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
    )
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
    )
    public static class PaginatedSection {
        @ValueMapValue
        private String paginatedStep;
        
        @ValueMapValue
        private String paginatedTitle;
        
        @ValueMapValue
        private String paginatedHeadingLevel;
        
        @ValueMapValue
        private String paginatedPromoBannerLink;
        
        @ValueMapValue
        private String paginatedReferAFriendBannerLink;
        
        @ValueMapValue
        private String paginatedThankYouBannerLink;
        
        @ValueMapValue
        private String paginatedDescription;
        
        @ChildResource
        private List<Resource> paginatedFields;
        
        @ChildResource
        private List<Resource> paginatedDropdownFields;
        
        @ChildResource
        private List<Resource> paginatedCustomFields;
        
        @ChildResource
        private List<Resource> paginatedCards;

        @JsonProperty("paginatedStep")
        public String getPaginatedStep() {
            return paginatedStep;
        }

        @JsonProperty("paginatedTitle")
        public String getPaginatedTitle() {
            return paginatedTitle != null && !paginatedTitle.trim().isEmpty() ? paginatedTitle : null;
        }

        @JsonProperty("paginatedHeadingLevel")
        public String getPaginatedHeadingLevel() {
            return paginatedHeadingLevel != null && !paginatedHeadingLevel.trim().isEmpty() ? paginatedHeadingLevel : "h2";
        }

        @JsonProperty("paginatedPromoBannerLink")
        public String getPaginatedPromoBannerLink() {
            return paginatedPromoBannerLink;
        }

        @JsonProperty("paginatedReferAFriendBannerLink")
        public String getPaginatedReferAFriendBannerLink() {
            return paginatedReferAFriendBannerLink;
        }

        @JsonProperty("paginatedThankYouBannerLink")
        public String getPaginatedThankYouBannerLink() {
            return paginatedThankYouBannerLink;
        }

        @JsonProperty("paginatedDescription")
        public String getPaginatedDescription() {
            return paginatedDescription != null && !paginatedDescription.trim().isEmpty() ? paginatedDescription : null;
        }

        @JsonProperty("paginatedFields")
        public List<FormField> getPaginatedFields() {
            if (paginatedFields == null || paginatedFields.isEmpty()) {
                return Collections.emptyList();
            }
            
            List<FormField> fieldsList = new ArrayList<>();
            for (Resource fieldResource : paginatedFields) {
                try {
                    FormField field = fieldResource.adaptTo(FormField.class);
                    if (field != null) {
                        fieldsList.add(field);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            return fieldsList;
        }

        @JsonProperty("paginatedDropdownFields")
        public List<DropdownField> getPaginatedDropdownFields() {
            if (paginatedDropdownFields == null || paginatedDropdownFields.isEmpty()) {
                return Collections.emptyList();
            }
            
            List<DropdownField> fieldsList = new ArrayList<>();
            for (Resource fieldResource : paginatedDropdownFields) {
                try {
                    DropdownField field = fieldResource.adaptTo(DropdownField.class);
                    if (field != null) {
                        fieldsList.add(field);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            return fieldsList;
        }

        @JsonProperty("paginatedCustomFields")
        public List<CustomField> getPaginatedCustomFields() {
            if (paginatedCustomFields == null || paginatedCustomFields.isEmpty()) {
                return Collections.emptyList();
            }
            
            List<CustomField> fieldsList = new ArrayList<>();
            for (Resource fieldResource : paginatedCustomFields) {
                try {
                    CustomField field = fieldResource.adaptTo(CustomField.class);
                    if (field != null) {
                        fieldsList.add(field);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            return fieldsList;
        }

        @JsonProperty("paginatedCards")
        public List<Card> getPaginatedCards() {
            if (paginatedCards == null || paginatedCards.isEmpty()) {
                return Collections.emptyList();
            }
            
            List<Card> cardsList = new ArrayList<>();
            for (Resource cardResource : paginatedCards) {
                try {
                    Card card = cardResource.adaptTo(Card.class);
                    if (card != null) {
                        cardsList.add(card);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            return cardsList;
        }

    }

    /**
     * Dropdown Field data object
     */
    @Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
    )
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
    )
    public static class DropdownField {
        @ValueMapValue
        private String label;
        
        @ChildResource
        private List<Resource> values;
        
        // Default constructor for Sling Model adaptation
        public DropdownField() {}

        @JsonProperty("label")
        public String getLabel() {
            return label != null ? label : "";
        }

        @JsonProperty("values")
        public List<DropdownValue> getValues() {
            if (values == null || values.isEmpty()) {
                return Collections.emptyList();
            }
            
            List<DropdownValue> valuesList = new ArrayList<>();
            for (Resource valueResource : values) {
                try {
                    DropdownValue dropdownValue = valueResource.adaptTo(DropdownValue.class);
                    if (dropdownValue != null) {
                        valuesList.add(dropdownValue);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            return valuesList;
        }
    }

    /**
     * Dropdown Value data object for individual dropdown options
     */
    @Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
    )
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
    )
    public static class DropdownValue {
        @ValueMapValue
        private String label;
        
        @ValueMapValue
        private String value;
        
        public DropdownValue() {}

        @JsonProperty("label")
        public String getLabel() {
            return label != null ? label : "";
        }

        @JsonProperty("value")
        public String getValue() {
            return value != null ? value : "";
        }
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
        
        // Default constructor for Sling Model adaptation
        public CustomField() {}
        
        // Constructor for manual creation (legacy array conversion)
        public CustomField(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @JsonProperty("key")
        public String getKey() {
            return key;
        }

        @JsonProperty("value")
        public String getValue() {
            return value;
        }
    }

    /**
     * Nested class for card items
     * Contains title, description, and image for card presentation
     */
    @Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
    )
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
    )
    public static class Card {
        @ValueMapValue
        private String cardTitle;
        
        @ValueMapValue
        private String cardDescription;
        
        @ValueMapValue
        private String cardImage;
        
        @ValueMapValue
        private String imageAlt;
        
        @ValueMapValue
        private String value;
        
        @ValueMapValue
        private Boolean isProductPage;
        
        // Default constructor for Sling Model adaptation
        public Card() {}
        
        // Constructor for manual creation
        public Card(String cardTitle, String cardDescription, String cardImage) {
            this.cardTitle = cardTitle;
            this.cardDescription = cardDescription;
            this.cardImage = cardImage;
        }
        
        // Constructor for manual creation with imageAlt
        public Card(String cardTitle, String cardDescription, String cardImage, String imageAlt) {
            this.cardTitle = cardTitle;
            this.cardDescription = cardDescription;
            this.cardImage = cardImage;
            this.imageAlt = imageAlt;
        }
        
        // Constructor for manual creation with imageAlt and value
        public Card(String cardTitle, String cardDescription, String cardImage, String imageAlt, String value) {
            this.cardTitle = cardTitle;
            this.cardDescription = cardDescription;
            this.cardImage = cardImage;
            this.imageAlt = imageAlt;
            this.value = value;
        }
        
        // Constructor for manual creation with all properties
        public Card(String cardTitle, String cardDescription, String cardImage, String imageAlt, String value, Boolean isProductPage) {
            this.cardTitle = cardTitle;
            this.cardDescription = cardDescription;
            this.cardImage = cardImage;
            this.imageAlt = imageAlt;
            this.value = value;
            this.isProductPage = isProductPage;
        }
        


        @JsonProperty("cardTitle")
        public String getCardTitle() {
            return cardTitle != null ? cardTitle : "";
        }

        @JsonProperty("cardDescription")
        public String getCardDescription() {
            return cardDescription != null ? cardDescription : "";
        }

        @JsonProperty("cardImage")
        public String getCardImage() {
            return cardImage != null ? cardImage : "";
        }

        @JsonProperty("imageAlt")
        public String getImageAlt() {
            return imageAlt != null ? imageAlt : "";
        }
        
        @JsonProperty("value")
        public String getValue() {
            return value != null ? value : "";
        }
        
        @JsonProperty("isProductPage")
        public boolean isProductPage() {
            return isProductPage != null && isProductPage;
        }
        

    }
}
