package dsm.foundation.core.models.impl.v1;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.day.crx.JcrConstants;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dsm.foundation.core.utils.DsmUtils;
import dsm.foundation.core.models.CommonComponentModel;
import dsm.foundation.core.models.PageComponentModel;
import dsm.foundation.core.services.ApiEndPointOsgiConfigs;
import dsm.foundation.core.caconfig.DataLayerConfigurationList;
import dsm.foundation.core.caconfig.GlobalConfiguration;
import dsm.foundation.core.caconfig.SiteMapXmlConfigurations;
import dsm.foundation.core.constants.DsmConstants;
import dsm.foundation.core.exception.DsmFoundationalException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
// import java.util.Collection;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Iterator;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { PageComponentModel.class,
		ComponentExporter.class }, resourceType = PageComponentModelImpl.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@JsonIgnoreProperties({ "currentDate" })
public class PageComponentModelImpl extends CommonComponentModel implements PageComponentModel {

	protected static final Logger LOGGER = LoggerFactory.getLogger(PageComponentModelImpl.class);
    protected static final List<String> urlPatterns = Arrays.asList(".*/[^/]*?/orders",".*/[^/]*?/services",".*/[^/]*?/legal",".*/[^/]*?/about-us",".*/[^/]*?/my-account",".*/[^/]*?/locations",".*/[^/]*?/for-professionals");
    public static final String LAST_MODIFIED = "lastModified";    
	static final String RESOURCE_TYPE = "aem-dsm-foundation/components/page";
	public static final String NOT_AVAILABLE = "n/a";
	public static final String SEPARATOR = ":";
	public static final String PIPE_DELIM = "|";
	static final String JCR_CREATED_DATE = "jcr:created";
    static final String CQ_LAST_MODIFIED = com.day.cq.wcm.api.NameConstants.PN_PAGE_LAST_MOD;
    private static final String TITLE = "title";
    private static final String ARTICLE = "article";
    private static final String DEFAULT_SITE = "kohler";
	public static final String LAST_REPLICATED = "lastReplicated";
    protected static final List<String> eCommerceUrlPatterns = Arrays.asList(".*/[^/]*?/cart",".*/[^/]*?/checkout",".*/[^/]*?/order-confirmation");

    //Added to reduce the calls to getPageType where the configuration is being resolved.
    private String datalayerPageName;

	@Self
	private SlingHttpServletRequest request;

	@Self
	private com.adobe.cq.wcm.core.components.models.Page pageComponentProperties;

    @Inject
	private ApiEndPointOsgiConfigs apiEndPointOsgiConfigs;

	@OSGiService
	private PublishUtils publishUtils;    

	public Page getCurrentPage() {
		PageManager pageManager = this.request.getResource().getResourceResolver().adaptTo(PageManager.class);
		return pageManager.getContainingPage(this.request.getResource());
	}
    
    public String getGlobalScript() {
        String globalScript = getCurrentPage().getProperties().get(DsmConstants.GLOBAL_SCRIPT,"");
        if (StringUtils.isEmpty(globalScript)) {
            final InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(getCurrentPage().getContentResource());
            final String inheritedValue = ivm.getInherited(DsmConstants.GLOBAL_SCRIPT, String.class);
            return inheritedValue;
        }
        final String globalScriptChomp = StringUtils.isNotBlank(globalScript) ? globalScript.replaceAll("\\r\\n|\\r|\\n", " ") : StringUtils.EMPTY;
        LOGGER.info("PageComponentDatalayer globalScript {}", globalScriptChomp);
        return globalScript;
    }

	private String getInheritedPageProperty(String propertyName) {
		InheritanceValueMap inheritanceValueMap = new HierarchyNodeInheritanceValueMap(getCurrentPage().getContentResource());
		return inheritanceValueMap.getInherited(propertyName, String.class);
	}

	public String getHeaderExpFrg() {
		return getInheritedPageProperty(DsmConstants.HEADER_XF);
	}

	public String getFooterExpFrg() {
		return getInheritedPageProperty(DsmConstants.FOOTER_XF);
	}

	@Override
	public com.adobe.cq.wcm.core.components.models.Page getPageComponentProperties() {
		return pageComponentProperties;
	}

	public String getTopHeadScripts() {
        String topHeadScript = getCurrentPage().getProperties().get(DsmConstants.TOP_HEAD_SCRIPTS,"");
        if(StringUtils.isEmpty(topHeadScript)){
            final InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(getCurrentPage().getContentResource());
            final String inheritedValue = ivm.getInherited(DsmConstants.TOP_HEAD_SCRIPTS, String.class);
            return inheritedValue;
        }
        final String topHeadScriptChomp = StringUtils.isNotBlank(topHeadScript) ? topHeadScript.replaceAll("\\r\\n|\\r|\\n", " ") : StringUtils.EMPTY;
        LOGGER.info("PageComponentDatalayer topHeadScript {}", topHeadScriptChomp);
        return topHeadScript;
    }

    public String getBottomHeadScripts() {
        return getInheritedPageProperty(DsmConstants.BOTTOM_HEAD_SCRIPTS);
    }

    public String getBodyStartScripts() {
        return getInheritedPageProperty(DsmConstants.BODY_START_SCRIPTS);
    }

    public String getFooterScripts() {
       String footerScript = getCurrentPage().getProperties().get(DsmConstants.FOOTER_SCRIPTS,"");
        if (StringUtils.isEmpty(footerScript)) {
            final InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(getCurrentPage().getContentResource());
            final String inheritedValue = ivm.getInherited(DsmConstants.FOOTER_SCRIPTS, String.class);
            return inheritedValue;
        }
        final String footerScriptChomp = StringUtils.isNotBlank(footerScript) ? footerScript.replaceAll("\\r\\n|\\r|\\n", " ") : StringUtils.EMPTY;
        LOGGER.info("PageComponentDatalayer footerSCript {}", footerScriptChomp);
    	return footerScript;
    }

    public String getSeoSchema() {
        return getCurrentPage().getProperties().get(DsmConstants.SEO_SCHEMA, "");
    }

     public String getSeoPageScript() {
    	String seoPageScript = getCurrentPage().getProperties().get(DsmConstants.SEO_PAGE_SCRIPT,"");
        if (StringUtils.isEmpty(seoPageScript)) {
            final InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(getCurrentPage().getContentResource());
            return ivm.getInherited(DsmConstants.SEO_SITE_SCRIPT, String.class);
        }
        
    	return seoPageScript;
    }

    public String getLanguage(){
        return getCurrentPage().getLanguage().getLanguage();
    }

    // Get the region details
    public String getCountry(){
        return getCurrentPage().getLanguage().getCountry();
    }

    // Get the Marketting campaign details
    public String getMktCampaign(){
        String cid = request.getParameter("cid");
        if (StringUtils.isNotEmpty(cid)){
            return cid;
        }
        return NOT_AVAILABLE;
    }

     /**
     * Returns current Date time
     *
     * @return
     */
    public String getCurrentDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("CST"));
        return format.format(new Date());
    }

	public String getAdobeAnalytic() {
    	ResourceResolver resourceResolver = DsmUtils.getServiceResourceResolver(DsmConstants.CONFIGURATION_META_DATA_EDITOR_SERVICE);
    	Resource pageResource = null;
        String adobeAnalytic =null;
		try {
			pageResource = DsmUtils.getPageResourceFromPath(getCurrentPage().getPath(), resourceResolver);
            ConfigurationBuilder adobeCb = pageResource != null ? pageResource.adaptTo(ConfigurationBuilder.class) : null;
            if (adobeCb != null) {
                GlobalConfiguration globalConfiguration = adobeCb.as(GlobalConfiguration.class);
                adobeAnalytic = globalConfiguration.adobeAnalytics();
                final String adobeAnalyticChomp = StringUtils.isNotBlank(adobeAnalytic) ? adobeAnalytic.replaceAll("\\r\\n|\\r|\\n", " ") : StringUtils.EMPTY;
                LOGGER.info("PageComponentDatalayer adobe Analytics", adobeAnalyticChomp);
            }
		} catch (DsmFoundationalException e) {
			 LOGGER.error("Unable to get adobe analytics js", e);
		}finally {
            DsmUtils.closeResourceResolver(resourceResolver);
        }
        return adobeAnalytic;
    }

	@Override
	public String getExportedType() {
		return request.getResource().getResourceType();
	}

     // Method to get full page URL
    public String getURL() {
        final String queryString = request.getQueryString();
        if(request.getRequestURL().toString().contains("model.json") && getSiteName() != null) {
            SiteMapXmlConfigurations siteMapConfigs = getSitemapConfigs();
            String removablePath = siteMapConfigs != null && siteMapConfigs.removablePath() != null ? siteMapConfigs.removablePath() : "";
        		return getHostUrl().toString() + request.getPathInfo().replace(removablePath, "");
        } else {
	        return queryString == null ? request.getRequestURL().toString() : request.getRequestURL().append('?')
	                .append(queryString)
	                .toString();
        }
    }

    // Get the sitename from parent page
	public String getSiteName() {
		GlobalConfiguration globalConfiguration = getglobalConfig();
		if (globalConfiguration != null && StringUtils.isNotEmpty(globalConfiguration.siteName())) {
			LOGGER.info("PageComponentDatalayer getSiteName {}", globalConfiguration.siteName().toLowerCase());
			return globalConfiguration.siteName().toLowerCase();
		} else {
			LOGGER.info("PageComponentDatalayer getSiteName {}", DEFAULT_SITE);
			return DEFAULT_SITE;
		}
	}

    // method to get details for article page
    public Map<String, Object> getArticleDetails(){
        Resource resource = this.request.getResource();
        Map<String, Object> articleDetails = new HashMap<>();
        ValueMap properties = resource.getValueMap();
        List<String> tagNames = Arrays.stream(getCurrentPage().getTags()).map(tag -> tag.getName()).collect(Collectors.toList());
        articleDetails.put("tags", String.join(PIPE_DELIM, tagNames));
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        if(properties.get(JcrConstants.JCR_CREATED) != null) {
            articleDetails.put(LAST_REPLICATED, format.format(properties.get(JcrConstants.JCR_CREATED, Date.class)));
        } else{
            articleDetails.put(LAST_REPLICATED,"n/a");
        }
        if(properties.get(CQ_LAST_MODIFIED) != null) {
            articleDetails.put(LAST_MODIFIED, format.format(properties.get(CQ_LAST_MODIFIED, Date.class)));
        } else{
            articleDetails.put(LAST_MODIFIED,"n/a");
        }
        String title = getCurrentPage().getTitle();
        if(title != null) {
            articleDetails.put(TITLE, title.replaceAll("\\r\\n|\\r|\\n|[-]", " ").replaceAll("<.*?>", "").toLowerCase().trim());
        } else {
            articleDetails.put(TITLE,"n/a");
        }
        LOGGER.info("PageComponentDatalayer get details for article page {}", articleDetails);
        return articleDetails;
    }

    /**
     * Get the pageName for datalayer based on the configured templateName for each page,
     * if not found by the template then check with the URL.
     * @return
     */
    public String getPageName(){
        datalayerPageName = getCurrentPage().getName().replace("-", " ");
        String pageType = getPageType();
        final String parentName = getParentName(datalayerPageName);
        if("plp".equalsIgnoreCase(pageType)){
            datalayerPageName = pageType + ":" + getCurrentPage().getParent().getName() + ":" + datalayerPageName;
        }
        else if("inspiration".equalsIgnoreCase(pageType) && StringUtils.equalsIgnoreCase(getSiteName(), DsmConstants.SITE_NAME_ANNSACKS)){
            datalayerPageName = pageType + ":" + datalayerPageName;
        }
        else if("pdp".equalsIgnoreCase(pageType)){
            datalayerPageName = pageType + ":" + getCurrentPage().getParent().getParent().getName() + ":" + datalayerPageName;
        }else if("pxp".equalsIgnoreCase(pageType)){
            datalayerPageName = pageType + ":" + getCurrentPage().getParent().getName() + ":" + datalayerPageName;            
        } else if(datalayerPageName.equalsIgnoreCase(getLanguage().toLowerCase())){
            datalayerPageName = "home";
        } else if (StringUtils.isNotEmpty(parentName)){
            if ("saunas".equals(datalayerPageName)) {
                return pageType + ":" + parentName;
            }
            return parentName;
        } else if(StringUtils.isNotBlank(pageType) && !datalayerPageName.startsWith(pageType) && !"ecommerce".equals(pageType)) {
            datalayerPageName = pageType + ":" + datalayerPageName;
        }
        LOGGER.info("PageComponentDatalayer pageName for datalayer based on the configured templateName {}", datalayerPageName);
        return datalayerPageName;
    }

    public String getParentName(String parentPageName){
        String pageName = StringUtils.EMPTY;
        String grandParentName = getCurrentPage().getParent().getParent().getName();
        String parentName = getCurrentPage().getParent().getName();
        String concatedParentPageName = parentName.replaceAll("-"," ").concat(":") + parentPageName;
        if (isParentFromList(grandParentName)) {
            return grandParentName.replaceAll("-"," ").concat(":") + concatedParentPageName;
        }
        if (isParentFromList(parentName)){
            return concatedParentPageName;
        }
        LOGGER.info("PageComponentDatalayer get parent pageName for datalayer {}", pageName);
        return pageName;
    }

    private boolean isParentFromList(final String parent) {
        String parents[] = {"inspiration", "shop-the-room", "wellness-experiences"};
        return ArrayUtils.contains(parents, parent);
    }

    // Get the first portion of pageName as deliminated by ':'
    public String getPrimaryCategory(){
        String[] pageNameArr = getDatalayerPageName().split(SEPARATOR);
        if(pageNameArr.length > 0) {
            return this.getSiteName() + SEPARATOR + pageNameArr[0];
        }
        return NOT_AVAILABLE;
    }

    /**
     * Fetch the pageType/Category from the URL pattern or the configuration
     * based on template.
     *
     * @return
     */
    public String getPageType(){
        if(getCurrentPage().getName().equalsIgnoreCase(getLanguage().toLowerCase())){
            return "home";
        }
        //First check with the configured URL before resolving the config.
        for(String regex : eCommerceUrlPatterns){
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(request.getRequestURL());
            boolean matchFound = matcher.find();
            if( matchFound) { 
                    return "ecommerce";
                }
            }
     

        for(String regex : urlPatterns){
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(request.getRequestURL());
            boolean matchFound = matcher.find();
            if( matchFound) {
                String pageType = StringUtils.substringAfterLast(regex, "/");
                String requestPath = request.getPathInfo();
                String pageName = requestPath.substring(requestPath.indexOf(pageType));
                if(StringUtils.isNotBlank(pageName) && pageName.indexOf(".html") > -1){
                    pageName = pageName.substring(0, pageName.indexOf(".html"));
                }
                datalayerPageName = pageName.replace("/", ":").replace("-", " ").replace(".model.json","");
                return pageType.replace("-"," ");
            }
        }

        String currentTemplateName = getCurrentPage().getTemplate().getName();
        String currentPagePath = getCurrentPage().getPath();
        ConfigurationBuilder configBuilder = request.getResource().adaptTo(ConfigurationBuilder.class);
        if (configBuilder != null) {
        Collection<DataLayerConfigurationList> configs = configBuilder
                .asCollection(DataLayerConfigurationList.class);
        Iterator<DataLayerConfigurationList> iterator = configs.iterator();
        while (iterator.hasNext()) {
            DataLayerConfigurationList next = iterator.next();
            if(currentTemplateName.equalsIgnoreCase(next.templateName()) || currentPagePath.equalsIgnoreCase(next.templateName()) ) { 
                return next.pageName();
            }
        }
        }
        LOGGER.info("PageComponentDatalayer the pageType/Category from the URL pattern for datalayer {}", getCurrentPage().getName().replace("-", " ").toLowerCase());
        return getCurrentPage().getName().replace("-", " ").toLowerCase();
    }

    // Get the first and second portion of pageName as delineated by ':'
    public String getSubCategory1(){
        String[] pageNameArr = getDatalayerPageName().split(SEPARATOR);
        String currentTemplateName = getCurrentPage().getTemplate().getName();
        if(pageNameArr.length > 1) {
            if ("pxp".equalsIgnoreCase(currentTemplateName)) {
                return getPrimaryCategory();
            }else {
        	LOGGER.info("PageComponentDatalayer the first and second portion of pageName {}", getPrimaryCategory() + SEPARATOR + pageNameArr[1]);
                return getPrimaryCategory() + SEPARATOR + pageNameArr[1];
            }
        }
        return NOT_AVAILABLE;
    }
    // Get the first, second and third portion of pageName as delineated by ':'
    public String getSubCategory2(){
        String currentTemplateName = getCurrentPage().getTemplate().getName();        
        String[] pageNameArr = getDatalayerPageName().split(SEPARATOR);
        if (pageNameArr.length > 1) {
            if ("pxp".equalsIgnoreCase(currentTemplateName)) {
                return getSubCategory1() + SEPARATOR + pageNameArr[pageNameArr.length -1];                
            }
            else if(pageNameArr.length > 2) {
                LOGGER.info("PageComponentDatalayer the first, second and third portion of pageName {}", getSubCategory1() + SEPARATOR + pageNameArr[2]);
                return getSubCategory1() + SEPARATOR + pageNameArr[2];
            }
        }
        return NOT_AVAILABLE;
    }

    // Get the article ID if present
    public String getSubCategoryID(){
        String[] pageNameArr = getDatalayerPageName().split(SEPARATOR);
        if(pageNameArr.length > 0) {
            return pageNameArr[pageNameArr.length - 1];
        }
        return NOT_AVAILABLE;
    }

     public Map<String, Object> getCategoryData(){
            Map<String, Object> cCategoryProperties = new HashMap<>();
            String currentTemplateName = getCurrentPage().getTemplate().getName();
            cCategoryProperties.put("primaryCategory", this.getPrimaryCategory() );
            cCategoryProperties.put("subCategory1", this.getSubCategory1());
            cCategoryProperties.put("subCategory2", this.getSubCategory2());
            cCategoryProperties.put("subCategoryID", this.getSubCategoryID());
            cCategoryProperties.put("subCategoryID2", NOT_AVAILABLE);
            cCategoryProperties.put("pageType", this.getPageType());
            if ("pxp".equalsIgnoreCase(currentTemplateName)){
                cCategoryProperties.put("subCategoryName", this.getSubCategoryID());
                cCategoryProperties.put("productLocalCategory", getCurrentPage().getProperties().get("contentIdentificationCategory", NOT_AVAILABLE).toLowerCase());
                cCategoryProperties.put("room", getCurrentPage().getProperties().get("contentIdentificationRoom", NOT_AVAILABLE).toLowerCase());
            }
            LOGGER.info("PageComponentDatalayer getCategoryData {}", cCategoryProperties);
            return cCategoryProperties;
    }

    /**
     * Added the placeholder object, the actual data would be updated from react
     * @return
     */
    public Map<String, Object> getUserData(){
            Map<String, Object> cUserProperties = new HashMap<>();
            cUserProperties.put("authStatus", "anonymous");
            cUserProperties.put("profileRole", NOT_AVAILABLE );
            cUserProperties.put("returningStatus", "new visitor");
            cUserProperties.put("customerProspect", NOT_AVAILABLE );
            cUserProperties.put("knownStatus", "unknown" );
            cUserProperties.put("envName", "development");
            cUserProperties.put("adBlocker", false);
            LOGGER.info("PageComponentDatalayer getUserData {}", cUserProperties);
            return cUserProperties;
    }
    
    public Map<String, Object> getSiteData(){
            ConfigurationBuilder siteConfigBuilder = request.getResource().adaptTo(ConfigurationBuilder.class);
            GlobalConfiguration configs = siteConfigBuilder != null ? siteConfigBuilder.as(GlobalConfiguration.class) : null;
            Map<String, Object> cSiteProperties = new HashMap<>();
            cSiteProperties.put("language", getCurrentPage().getLanguage().getLanguage().toLowerCase());
            cSiteProperties.put("siteName", this.getSiteName());
            // Fetching region details from getGeoDetails method
            cSiteProperties.put("geoRegion", getCountry() != null ? getCountry().toLowerCase() : NOT_AVAILABLE);
            // Populated from javascript
            cSiteProperties.put("platform", NOT_AVAILABLE);
            // populated from javascript
            cSiteProperties.put("responsiveFormat", NOT_AVAILABLE);
            cSiteProperties.put("appVersion", "aem 6.5.9");
            // Populated from javascript
            cSiteProperties.put("orientation", NOT_AVAILABLE);
            // Populat targetID from CA-Configs
            cSiteProperties.put("targetPropertyID", configs != null && configs.targetPropertyID() != null ? configs.targetPropertyID() : NOT_AVAILABLE);
            // Populat environment from APIendpoint OSGI configs
            cSiteProperties.put("environment", apiEndPointOsgiConfigs != null && apiEndPointOsgiConfigs.environmentName() != null ? apiEndPointOsgiConfigs.environmentName() : NOT_AVAILABLE);
            LOGGER.info("PageComponentDatalayer getSiteData {}", cSiteProperties.toString());
            return cSiteProperties;
    }

    public Map<String, Object> getPageData() throws RepositoryException{
            //Create a map of properties we want to expose
            Map<String, Object> cPageProperties = new HashMap<>();
            cPageProperties.put("key" +
                    "words", getCurrentPage().getProperties().get("keywords") != null ? getCurrentPage().getProperties().get("keywords") : "n/a");
            cPageProperties.put("timestamp", getCurrentDate());
            cPageProperties.put("language", getLanguage());

            String currentTemplateName = getCurrentPage().getTemplate().getName();
            
            if ("pxp".equalsIgnoreCase(currentTemplateName)){
                cPageProperties.put("pageName", getSubCategory2());
            }else {
                cPageProperties.put("pageName", this.getSiteName() + ":" + this.getPageName());
            }
            cPageProperties.put("referrer", this.request.getHeader(HttpHeaders.REFERER));
            cPageProperties.put("pageUrl", this.getURL());
            cPageProperties.put("redirect", getCurrentPage().getProperties().get("redirectTarget") != null ? getCurrentPage().getProperties().get("redirectTarget") : "n/a");
                    String thumbnail = getCurrentPage().getProperties("image") != null
                    ? (String) getCurrentPage().getProperties("image").get("fileReference")
                    : "";
            cPageProperties.put("thumbnailImageUrl", DsmUtils.getImageLink(thumbnail, request.getResourceResolver(), publishUtils));
            cPageProperties.put("mktgCampaign", this.getMktCampaign());
            cPageProperties.put("description", getCurrentPage().getProperties().get("jcr:description") != null ? getCurrentPage().getProperties().get("jcr:description") : "");
            cPageProperties.put("title", getCurrentPage().getProperties().get("jcr:title") != null ? getCurrentPage().getProperties().get("jcr:title") : "");
            //populated via javascript
            cPageProperties.put("previousPage", "n/a");
            cPageProperties.put("contentType", getPageType());
            cPageProperties.put("contentIdentificationRoom", getCurrentPage().getProperties().get("contentIdentificationRoom"));
            cPageProperties.put("contentIdentificationCategory", getCurrentPage().getProperties().get("contentIdentificationCategory"));
            cPageProperties.put("contentIdentificationContentType", getCurrentPage().getProperties().get("contentIdentificationContentType"));
            cPageProperties.put("contentIdentificationPurpose", getCurrentPage().getProperties().get("contentIdentificationPurpose"));
            cPageProperties.put("contentIdentificationCollection", getCurrentPage().getProperties().get("contentIdentificationCollection"));
            cPageProperties.put("contentIdentificationDesignStyle", getCurrentPage().getProperties().get("contentIdentificationDesignStyle"));
            cPageProperties.put("id", pageComponentProperties != null ? pageComponentProperties.getId() : NOT_AVAILABLE);
            cPageProperties.put("cannonicalUrl", getCurrentPage().getProperties().get("cq:canonicalUrl"));
            //Return map of properties 
            LOGGER.info("PageComponentDatalayer getPageData {}", cPageProperties);
            return cPageProperties;
    }

    public Map<String, Object> getArticleData(){
        //Create a map of properties we want to expose
        Map<String, Object> cArticleDetails = this.getArticleDetails();
        Map<String, Object> cArticleProperties = new HashMap<>();
        cArticleProperties.put(TITLE, cArticleDetails.get(TITLE));
        cArticleProperties.put("tags", cArticleDetails.get("tags"));
        cArticleProperties.put("readTime", "n/a");
        cArticleProperties.put("publishDate", cArticleDetails.get(LAST_REPLICATED));
        cArticleProperties.put("lastUpdateDate", cArticleDetails.get(LAST_MODIFIED));
        cArticleProperties.put("wordCount", "");
        cArticleProperties.put("imageImpression", "n/a");
        // Return map of properties
        LOGGER.info("PageComponentDatalayer getArticleData {}", cArticleProperties);
        return cArticleProperties;
    }

    @Override
    public String getData() throws RepositoryException {
        Resource resource = this.request.getResource();
        if (ComponentUtils.isDataLayerEnabled(resource)) {  
        	//Create a map of properties we want to expose
            Map<String, Object> cProperties = new HashMap<>();
            cProperties.put("pageInfo", this.getPageData());
            cProperties.put("category", this.getCategoryData());
            cProperties.put("site", this.getSiteData());
            cProperties.put("user", this.getUserData());
            if(getPageType() != null && getPageType().startsWith(ARTICLE)){
                cProperties.put(ARTICLE, this.getArticleData());
            }
            //Return the properties as a JSON String
            try {
                return new ObjectMapper().writeValueAsString(cProperties);
            } catch (JsonProcessingException e) {  
                LOGGER.error("Unable to generate dataLayer JSON string", e);
            }
        }
        // return null if the Data Layer is not enabled
        return null;
    }

	 public GlobalConfiguration getglobalConfig()  {
        if(request.getResource() != null && this.request.getResource().getParent() != null){
            ConfigurationBuilder cb = this.request.getResource().getParent().adaptTo(ConfigurationBuilder.class);
	        return cb != null ? cb.as(GlobalConfiguration.class) : null;
	    }
        return null;
    }
	 
    public String getDatalayerPageName(){
    	if (datalayerPageName != null) {
           return datalayerPageName;
       }
		return getPageName();
    }

    public String getEventPageType(){
        return getCurrentPageType(getCurrentPage(), request);
    }

    public String getStoreDataLayerLinkType() {
        return getDataLayerLinkType(getCurrentPage());
    }

    public String getPageUrl() {
        SiteMapXmlConfigurations siteMapConfigs = getSitemapConfigs();
        String removablePath = siteMapConfigs != null && siteMapConfigs.removablePath() != null ? siteMapConfigs.removablePath() : "";
        return getHostUrl() + (getCurrentPage() != null ? getCurrentPage().getPath().replaceAll(removablePath, "").replaceAll(".html", "") : "");
    }

    public String getCanonicalUrl() {
        SiteMapXmlConfigurations siteMapConfigs = getSitemapConfigs();
        String removablePath = siteMapConfigs != null && siteMapConfigs.removablePath() != null ? siteMapConfigs.removablePath() : "";
        if (getCurrentPage() != null && getCurrentPage().getProperties().get("canonicalUrl") != null) {
            return getHostUrl() + getCurrentPage().getProperties().get("canonicalUrl").toString().replaceAll(removablePath, "").replaceAll(".html", "");
        }
        return null;
    }

    private String getHostUrl() {
        SiteMapXmlConfigurations siteMapConfigs = getSitemapConfigs();
        String hostUrl = siteMapConfigs != null ? siteMapConfigs.hostUrl() : "";
    	LOGGER.info("PageComponentDatalayer for hostUrl datalayer {}", hostUrl);
        return StringUtils.defaultString(hostUrl);
    }

    private SiteMapXmlConfigurations getSitemapConfigs() {
        ConfigurationBuilder cb = request.getResource().adaptTo(ConfigurationBuilder.class);
        return cb != null ? cb.as(SiteMapXmlConfigurations.class) : null;
    }

    public boolean isEditorMode() {
        LOGGER.info("Current JVM version - " + System. getProperty("java.version"));
        return WCMMode.EDIT == WCMMode.fromRequest(request);
    }

    
    public Map<String,Object> getStoreData(){
    	Map<String,Object> storeData = new LinkedHashMap<>();
    	// storeData.put("bpNumber",this.getBPNumber());
    	// storeData.put("storeName", this.getStoreName());
    	// storeData.put("address", this.getAddress());
    	// storeData.put("city",this.getCity());
    	// storeData.put("state",this.getState());
    	// storeData.put("zip",this.getZip());
    	// storeData.put("microSiteUrl",this.getMicrositeUrl());
    	// storeData.put("logoImage",this.getLogoImage());
    	// storeData.put("storePhone" ,this.getStorePhone());
    	storeData.put("schema", this.getSchema());
    	return storeData;
    }
	private Object getSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put(DsmConstants.SCHEMA_IMAGE, this.getSchemaData(DsmConstants.SCHEMA_IMAGE));
        schema.put(DsmConstants.SCHEMA_NAME, this.getSchemaData(DsmConstants.SCHEMA_NAME));
        schema.put(DsmConstants.SCHEMA_DESCRIPTION, this.getSchemaData(DsmConstants.SCHEMA_DESCRIPTION));
        schema.put(DsmConstants.SCHEMA_HASMAP, this.getSchemaData(DsmConstants.SCHEMA_HASMAP));
        schema.put(DsmConstants.SCHEMA_URL, this.getSchemaData(DsmConstants.SCHEMA_URL));
        schema.put(DsmConstants.SCHEMA_TELEPHONE, this.getSchemaData(DsmConstants.SCHEMA_TELEPHONE));
        schema.put(DsmConstants.SCHEMA_PRICERANGE, this.getSchemaData(DsmConstants.SCHEMA_PRICERANGE));
        schema.put(DsmConstants.SCHEMA_STREET, this.getSchemaData(DsmConstants.SCHEMA_STREET));
        schema.put(DsmConstants.SCHEMA_LOCALITY, this.getSchemaData(DsmConstants.SCHEMA_LOCALITY));
        schema.put(DsmConstants.SCHEMA_REGION, this.getSchemaData(DsmConstants.SCHEMA_REGION));
        schema.put(DsmConstants.SCHEMA_ZIP, this.getSchemaData(DsmConstants.SCHEMA_ZIP));
        schema.put(DsmConstants.SCHEMA_COUNTRY, this.getSchemaData(DsmConstants.SCHEMA_COUNTRY));
        schema.put(DsmConstants.SCHEMA_LATITUDE, this.getSchemaData(DsmConstants.SCHEMA_LATITUDE));
        schema.put(DsmConstants.SCHEMA_LONGITUDE, this.getSchemaData(DsmConstants.SCHEMA_LONGITUDE));
        schema.put(DsmConstants.SCHEMA_WEEKOPENHOUR, this.getSchemaData(DsmConstants.SCHEMA_WEEKOPENHOUR));
        schema.put(DsmConstants.SCHEMA_WEEKCLOSEHOUR, this.getSchemaData(DsmConstants.SCHEMA_WEEKCLOSEHOUR));
        schema.put(DsmConstants.SCHEMA_SATOPENHOUR, this.getSchemaData(DsmConstants.SCHEMA_SATOPENHOUR));
        schema.put(DsmConstants.SCHEMA_SATCLOSEHOUR, this.getSchemaData(DsmConstants.SCHEMA_SATCLOSEHOUR));
        schema.put(DsmConstants.SCHEMA_SUNOPENHOUR, this.getSchemaData(DsmConstants.SCHEMA_SUNOPENHOUR));
        schema.put(DsmConstants.SCHEMA_SUNCLOSEHOUR, this.getSchemaData(DsmConstants.SCHEMA_SUNCLOSEHOUR));
        return schema;
    }
	@Override
    @JsonIgnore
    public String getSchemaData(String schemaType) {
        String schemaData = getCurrentPage().getProperties().get(schemaType, "");
        if (StringUtils.isEmpty(schemaData)) {
            final InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(getCurrentPage().getContentResource());
            return ivm.getInherited(schemaType, String.class);

        }
        return schemaData;
    }
	public String getMetaContent() {
        final InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(getCurrentPage().getContentResource());
        final String inheritedValue = ivm.getInherited(DsmConstants.INDEXINGDISABLED, String.class);
        return StringUtils.isNotBlank(inheritedValue) ? DsmConstants.META_CONTENT : "";
    }
	
	public String getAnalyticsScriptClass() {
        String analyticsScriptClass = getCurrentPage().getProperties().get(DsmConstants.ANALYTICS_SCRIPT_CLASS,"");
        if (StringUtils.isEmpty(analyticsScriptClass)) {
            final InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(getCurrentPage().getContentResource());
            final String inheritedValue = ivm.getInherited(DsmConstants.ANALYTICS_SCRIPT_CLASS, String.class);
            return inheritedValue;
        }
        final String analyticsScriptClassChomp = StringUtils.isNotBlank(analyticsScriptClass) ? analyticsScriptClass.replaceAll("\\r\\n|\\r|\\n", " ") : StringUtils.EMPTY;
        LOGGER.info("PageComponentDatalayer analytics script class name {}", analyticsScriptClassChomp);
        return analyticsScriptClass;
    }
    
    public String getSchemaJsonLD() {
        return getCurrentPage().getProperties().get("jsonLD", "");
    }
    
    public Map<String,Object> getSeoProperties() {
        Map<String,Object> properties = new LinkedHashMap<>();
        properties.put("email", this.getInheritedPageProperty("email"));
        properties.put("phoneNum", this.getInheritedPageProperty("phoneNum"));
        return properties;
    }

    public String getRedirectUrl() {
        return getCurrentPage().getProperties().get("cq:redirectTarget", "");
    }

    public String getRedirectType() {
        return getCurrentPage().getProperties().get("redirectType", "");
    }

}
