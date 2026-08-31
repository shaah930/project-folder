package dsm.foundation.core.models.impl;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.Via;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dsm.foundation.core.caconfig.GlobalConfiguration;
import dsm.foundation.core.caconfig.SiteMapXmlConfigurations;
import dsm.foundation.core.models.DynamicPDPModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Model(adaptables = SlingHttpServletRequest.class,
       adapters = { DynamicPDPModel.class, ComponentExporter.class },
       resourceType = DynamicPDPModelImpl.RESOURCE_TYPE,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class DynamicPDPModelImpl implements DynamicPDPModel {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicPDPModelImpl.class);

    static final String RESOURCE_TYPE = "aem-dsm-foundation/components/dynamicpdp/v1/dynamicpdp";

    @Self
    private SlingHttpServletRequest request;

    @ValueMapValue
    @Via("resource")
    private String metaTitle;

    @ValueMapValue
    @Via("resource")
    private String metaKeywords;

    @ValueMapValue
    @Via("resource")
    private String metaDescription;

    private Page currentPage;
    private String siteName;
    private String hostUrl;

    @PostConstruct
    protected void init() {
        PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
        if (pageManager != null) {
            currentPage = pageManager.getContainingPage(request.getResource());
        }

        ConfigurationBuilder cb = request.getResource().adaptTo(ConfigurationBuilder.class);
        if (cb != null) {
            GlobalConfiguration globalConfig = cb.as(GlobalConfiguration.class);
            if (globalConfig != null && StringUtils.isNotEmpty(globalConfig.siteName())) {
                siteName = globalConfig.siteName().toLowerCase();
            }

            SiteMapXmlConfigurations siteMapConfig = cb.as(SiteMapXmlConfigurations.class);
            if (siteMapConfig != null) {
                hostUrl = StringUtils.defaultString(siteMapConfig.hostUrl());
            }
        }

        if (siteName == null) {
            siteName = "annsacks-redesign";
        }
    }

    @Override
    public String getMetaTitle() {
        if (StringUtils.isNotEmpty(metaTitle)) {
            return metaTitle;
        }
        return currentPage != null ? currentPage.getTitle() : "";
    }

    @Override
    public String getMetaKeywords() {
        if (StringUtils.isNotEmpty(metaKeywords)) {
            return metaKeywords;
        }
        if (currentPage != null) {
            ValueMap props = currentPage.getProperties();
            return props.get("keywords", "");
        }
        return "";
    }

    @Override
    public String getMetaDescription() {
        if (StringUtils.isNotEmpty(metaDescription)) {
            return metaDescription;
        }
        if (currentPage != null) {
            return currentPage.getDescription() != null ? currentPage.getDescription() : "";
        }
        return "";
    }

    @Override
    public String getProductImage() {
        Object productImage = request.getAttribute("productImage");
        return productImage != null ? productImage.toString() : "";
    }

    @Override
    public String getPageUrl() {
        if (currentPage != null) {
            return hostUrl + currentPage.getPath();
        }
        return "";
    }

    @Override
    public String getCanonicalUrl() {
        if (currentPage != null) {
            ValueMap props = currentPage.getProperties();
            String canonical = props.get("canonicalUrl", String.class);
            if (StringUtils.isNotEmpty(canonical)) {
                return hostUrl + canonical;
            }
            return getPageUrl();
        }
        return "";
    }

    @Override
    public String getSiteName() {
        return siteName;
    }

    @Override
    public String getType() {
        return "Product";
    }

    @Override
    public String getAdditionalType() {
        return "";
    }

    @Override
    public String getProductName() {
        Object productName = request.getAttribute("productName");
        if (productName != null) {
            return productName.toString();
        }
        return getMetaTitle();
    }

    @Override
    public String getProductDescription() {
        Object productDesc = request.getAttribute("productDescription");
        if (productDesc != null) {
            return productDesc.toString();
        }
        return getMetaDescription();
    }

    @Override
    public String getSku() {
        Object sku = request.getAttribute("sku");
        if (sku != null) {
            return sku.toString();
        }
        String skuParam = request.getParameter("skuId");
        return skuParam != null ? skuParam : "";
    }

    @Override
    public String getCurrencyCode() {
        return "USD";
    }

    @Override
    public String getMainPrice() {
        Object price = request.getAttribute("mainPrice");
        return price != null ? price.toString() : "";
    }

    @Override
    public String getPrice() {
        Object price = request.getAttribute("price");
        return price != null ? price.toString() : getMainPrice();
    }

    @Override
    public String getInventoryStatus() {
        Object status = request.getAttribute("inventoryStatus");
        return status != null ? status.toString() : "InStock";
    }

    @Override
    public String getProductInfoJson() {
        Map<String, Object> productInfo = new HashMap<>();
        productInfo.put("productID", getSku());
        productInfo.put("productStatus", getInventoryStatus());
        productInfo.put("category", "n/a");

        try {
            return new ObjectMapper().writeValueAsString(productInfo);
        } catch (JsonProcessingException e) {
            LOG.error("Unable to generate productInfo JSON", e);
            return "{}";
        }
    }

    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }
}
