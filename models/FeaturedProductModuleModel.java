package dsm.foundation.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import org.osgi.annotation.versioning.ConsumerType;
import java.util.List;

@ConsumerType
public interface FeaturedProductModuleModel extends ComponentExporter {
    
    String getDsmModule();
    String getTitle();
    String getEyebrow();
    String getDescription();
    String getColorfinishestext();
    String getCardImage();
    List<FeaturedProductImagePair> getSwatchImages();
    List<FeaturedProductAccordionItem> getAccordions();
    String getExportedType();
}
