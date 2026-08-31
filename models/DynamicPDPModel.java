package dsm.foundation.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface DynamicPDPModel extends ComponentExporter {

    String getMetaTitle();

    String getMetaKeywords();

    String getMetaDescription();

    String getProductImage();

    String getPageUrl();

    String getCanonicalUrl();

    String getSiteName();

    String getType();

    String getAdditionalType();

    String getProductName();

    String getProductDescription();

    String getSku();

    String getCurrencyCode();

    String getMainPrice();

    String getPrice();

    String getInventoryStatus();

    String getProductInfoJson();
}
