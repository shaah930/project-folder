package dsm.foundation.core.models.impl.v1;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import dsm.foundation.core.models.FeaturedProductAccordionItem;
import dsm.foundation.core.models.FeaturedProductImagePair;
import dsm.foundation.core.models.FeaturedProductModuleModel;
import dsm.foundation.core.utils.AssetUtils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = { FeaturedProductModuleModel.class, ComponentExporter.class },
        resourceType = FeaturedProductModuleModelImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeaturedProductModuleModelImpl implements FeaturedProductModuleModel {

    public static final String RESOURCE_TYPE = "aem-dsm-foundation/components/featuredproductmodule/v1/featuredproductmodule";

    @Self
    private SlingHttpServletRequest request;

    @Inject
	PublishUtils publishUtils;

    @SlingObject
    private Resource resource;

    @ValueMapValue
    @Via("resource")
    private String dsmModule;

    @ValueMapValue
    @Via("resource")
    private String title;

    @ValueMapValue
    @Via("resource")
    private String eyebrow;

    @ValueMapValue
    @Via("resource")
    private String description;

    @ValueMapValue
    @Via("resource")
    private String colorfinishestext;

    private String cardImage;
    private List<FeaturedProductImagePair> swatchImages = Collections.emptyList();
    private List<FeaturedProductAccordionItem> accordions = Collections.emptyList();

    @PostConstruct
    private void init() {
        // Resolve card image from child node 'cardImage/fileReference' if present
        Resource card = resource.getChild("cardImage");
        if (card != null) {
            ValueMap cvm = card.getValueMap();
            this.cardImage = cvm.get("fileReference", String.class);
        }
        if (this.cardImage == null) {
            // Fallback to direct property if dialog stored it differently
            this.cardImage = resource.getValueMap().get("cardImage", String.class);
        }

        // Build swatch image pairs from composite multifield 'swatchimages'
        List<FeaturedProductImagePair> pairs = new ArrayList<>();
        Resource swatches = resource.getChild("swatchimages");
        if (swatches != null) {
            for (Resource item : swatches.getChildren()) {
                String swatchRef = null;
                String pairRef = null;
                Resource sw = item.getChild("swatchImage");
                if (sw != null) {
                    swatchRef = sw.getValueMap().get("fileReference", String.class);
                    if (swatchRef == null) {
                        swatchRef = sw.getValueMap().get("swatchImage", String.class);
                    }
                } else {
                    // Fallback: property on the item itself
                    swatchRef = item.getValueMap().get("swatchImage", String.class);
                }
                Resource pr = item.getChild("pairImage");
                if (pr != null) {
                    pairRef = pr.getValueMap().get("fileReference", String.class);
                    if (pairRef == null) {
                        pairRef = pr.getValueMap().get("pairImage", String.class);
                    }
                } else {
                    // Fallback: property on the item itself
                    pairRef = item.getValueMap().get("pairImage", String.class);
                }
                if (swatchRef != null || pairRef != null) {
                    String swatchResolved = swatchRef != null ? AssetUtils.getScene7AssetPath(swatchRef, resource, publishUtils) : null;
                    String swatchTitle = item.getValueMap().get("swatchTitle", String.class);
                    String pairResolved = pairRef != null ? AssetUtils.getScene7AssetPath(pairRef, resource, publishUtils) : null;
                    pairs.add(new FeaturedProductImagePair(swatchResolved, swatchTitle, pairResolved));
                }
            }
        }
        this.swatchImages = pairs;

        // Build accordion items from composite multifield 'accordions'
        List<FeaturedProductAccordionItem> accs = new ArrayList<>();
        Resource accNode = resource.getChild("accordions");
        if (accNode != null) {
            for (Resource item : accNode.getChildren()) {
                ValueMap vm = item.getValueMap();
                String at = vm.get("accordionTitle", String.class);
                String desc = vm.get("description", String.class);
                if (at != null || desc != null) {
                    FeaturedProductAccordionItem accordionItem = new FeaturedProductAccordionItem(at, desc);
                    accordionItem.setColorfinishestext(vm.get("colorfinishestext", String.class));
                    accs.add(accordionItem);
                }
            }
        }
        this.accordions = accs;
    }

    @Override
    public String getDsmModule() {
        return dsmModule;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getEyebrow() {                    
        return eyebrow;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getColorfinishestext() {
        return colorfinishestext;
    }

    public void setColorfinishestext(String colorfinishestext) {
        this.colorfinishestext = colorfinishestext;
    }
        
    @Override
    @JsonProperty("cardImage")
    public String getCardImage() {
        if (cardImage != null && !cardImage.isEmpty()) {
            return AssetUtils.getScene7AssetPath(cardImage, resource, publishUtils);
        }
        return null;
    }

    @Override
    public List<FeaturedProductImagePair> getSwatchImages() {
        return swatchImages;
    }

    @Override
    public List<FeaturedProductAccordionItem> getAccordions() {
        return accordions;
    }

    @Override
    public String getExportedType() {
        return resource != null ? resource.getResourceType() : "";
    }
}
