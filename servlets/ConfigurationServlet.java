package dsm.foundation.core.servlets;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import dsm.foundation.core.exception.DsmFoundationalException;
import javax.json.JsonException;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.jcr.contentloader.ContentTypeUtil;
import org.apache.sling.servlets.post.JSONResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.rest.Constants;
import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dsm.foundation.core.caconfig.APIEndPointConfigurationList;
import dsm.foundation.core.caconfig.AnnsacksPDPConfigurationList;
import dsm.foundation.core.caconfig.CommerceConfig;
import dsm.foundation.core.caconfig.GlobalConfiguration;
import dsm.foundation.core.caconfig.MarketingParamConfigurations;
import dsm.foundation.core.caconfig.PresetConfigurations;
import dsm.foundation.core.caconfig.RegexConfigurations;
import dsm.foundation.core.caconfig.RegexPatternConfigurationList;
import dsm.foundation.core.caconfig.SalesChannelConfig;
import dsm.foundation.core.caconfig.SecuredPagePathConfiguration;
import dsm.foundation.core.caconfig.SocialMediaConfigurationList;
import dsm.foundation.core.constants.DsmConstants;
import dsm.foundation.core.pojo.Global;
import dsm.foundation.core.pojo.GlobalConfigBean;
import dsm.foundation.core.pojo.Internationalization;
import dsm.foundation.core.pojo.MarketingParams;
import dsm.foundation.core.pojo.PasswordRegex;
import dsm.foundation.core.pojo.SessionAuth;
import dsm.foundation.core.services.SvgOSGiConfig;
import dsm.foundation.core.utils.DsmUtils;


@Component(
        service = Servlet.class,
        property = {
            ServletResolverConstants.SLING_SERVLET_PATHS + "=" + "/bin/kohler/services/systemconfig",
            ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES +"=" + ServletResolverConstants.DEFAULT_RESOURCE_TYPE,
            ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
            ServletResolverConstants.SLING_SERVLET_SELECTORS + "=systemconfig",
            ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=" + DsmConstants.JSON})
public class ConfigurationServlet extends SlingAllMethodsServlet {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationServlet.class);
    public static final String GLOBALCONFIG_SELECTOR = "systemconfig";
    private static final long serialVersionUID = 2598426539166789516L;
    private static final String CONTENT = "/content/";
    
    @Reference
    private transient SvgOSGiConfig svgOSGiConfig;
    @Override
    protected void doGet(final SlingHttpServletRequest servletRequest, final SlingHttpServletResponse servletResponse)
            throws IOException {        
        ResourceResolver resourceResolver = DsmUtils.getServiceResourceResolver(DsmConstants.CONFIGURATION_META_DATA_EDITOR_SERVICE);
        // Set response headers.
        LOGGER.info("In Conf servlet");
		servletResponse.setContentType(JSONResponse.RESPONSE_CONTENT_TYPE);
        servletResponse.setCharacterEncoding(Constants.DEFAULT_CHARSET);
        final PrintWriter out = servletResponse.getWriter();

        if (resourceResolver == null) {
            LOGGER.error("Unable to obtain service ResourceResolver for sub-service '{}'. "
                    + "Verify the service-user mapping is configured for this bundle.",
                    DsmConstants.CONFIGURATION_META_DATA_EDITOR_SERVICE);
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\" : \"Unable to obtain service ResourceResolver\"}");
            return;
        }

        String selectors[] = servletRequest.getRequestPathInfo().getSelectors();
        if(ArrayUtils.isEmpty(selectors)){
            servletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\" : \"SiteName is Mandatory\"}");
            DsmUtils.closeResourceResolver(resourceResolver);
            return;
        }
        String pagePath;
        JsonObject apiEndPointData;
        if(servletRequest.getRequestPathInfo().getResourcePath().contains("/bin/kohler")) {
        	pagePath = CONTENT+selectors[0].replaceAll("_", "/");
	        apiEndPointData = getApiEndPointData(pagePath);	        
        } else {
        	pagePath = servletRequest.getRequestPathInfo().getResourcePath();
        	Resource resource = resourceResolver.getResource(pagePath);
            if(resource == null) {
                Resource requestResource = servletRequest.getResource();
                Resource parent = requestResource != null ? requestResource.getParent() : null;
                if (parent != null) {
                    pagePath = parent.getPath();
                }
            }
            if (StringUtils.isBlank(pagePath)) {
                LOGGER.error("Unable to resolve page path for request '{}'", servletRequest.getRequestURI());
                servletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\" : \"Unable to resolve page path\"}");
                DsmUtils.closeResourceResolver(resourceResolver);
                return;
            }
        	apiEndPointData = getApiEndPointData(pagePath);		    
        }
        
        Global global = new Global();
        
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            Resource pageResource = DsmUtils.getPageResourceFromPath(pagePath, resourceResolver);
            GlobalConfiguration globalConfiguration = Objects.requireNonNull(pageResource.adaptTo(ConfigurationBuilder.class)).as(GlobalConfiguration.class);
            RegexConfigurations regexConfigurations = Objects.requireNonNull(pageResource.adaptTo(ConfigurationBuilder.class)).as(RegexConfigurations.class);
            MarketingParamConfigurations marketingParamConfigurations = Objects.requireNonNull(pageResource.adaptTo(ConfigurationBuilder.class)).as(MarketingParamConfigurations.class);      
            SecuredPagePathConfiguration securedPagePathConfiguration = Objects.requireNonNull(pageResource.adaptTo(ConfigurationBuilder.class)).as(SecuredPagePathConfiguration.class);
            
            PasswordRegex regex = new PasswordRegex();
            MarketingParams marketingParams = new MarketingParams();
            SessionAuth sessionAuth = new SessionAuth();
            
            Collection<RegexPatternConfigurationList> configs = pageResource.adaptTo(ConfigurationBuilder.class)
					.asCollection(RegexPatternConfigurationList.class);
			Iterator<RegexPatternConfigurationList> iterator = configs.iterator();
			JsonObject regexPattern = new JsonObject();
			JsonObject regexPatternMap = new JsonObject();
			while (iterator.hasNext()) {
				RegexPatternConfigurationList next = iterator.next();
				regexPatternMap.addProperty(next.patternKey(), next.patternValue());
			}
			regexPattern.add("map", regexPatternMap);
			
			JsonObject annsacksPDPConfig = new JsonObject();
			JsonObject annsacksPDPConfigMap = new JsonObject();
            Collection<AnnsacksPDPConfigurationList> annsacksPDPConfigItems = pageResource.adaptTo(ConfigurationBuilder.class)
            .asCollection(AnnsacksPDPConfigurationList.class);
            Iterator<AnnsacksPDPConfigurationList> iterator1 = annsacksPDPConfigItems.iterator();
            while(iterator1.hasNext()) {
                AnnsacksPDPConfigurationList next = iterator1.next();
                annsacksPDPConfigMap.addProperty(next.categoryName(), next.pagePath());
            }
            annsacksPDPConfig.add("map", annsacksPDPConfigMap);

            JsonObject salesChannelConfig = new JsonObject();
            JsonObject salesChannelConfigMap = new JsonObject();
            Collection<SalesChannelConfig> salesChannelConfigItems = pageResource.adaptTo(ConfigurationBuilder.class)
            .asCollection(SalesChannelConfig.class);
            Iterator<SalesChannelConfig> iterator2 = salesChannelConfigItems.iterator();
            while(iterator2.hasNext()) {
                SalesChannelConfig next = iterator2.next();
                salesChannelConfigMap.addProperty(StringUtils.defaultString(next.brandName()), StringUtils.defaultString(next.brandSalesChannel()));
            }
            salesChannelConfig.add("map", salesChannelConfigMap);

            regex.setRegex(DsmUtils.getValueIfValid(regexConfigurations.regex(), "Password Regex "));
            regex.setConditionsList(regexConfigurations.conditionsList());
            
            marketingParams.setMarketParamValues(marketingParamConfigurations.marketParamValues());

			final JsonObject presetConfigs = new JsonObject();
			final Collection<PresetConfigurations> presetConfigurations = pageResource
					.adaptTo(ConfigurationBuilder.class).asCollection(PresetConfigurations.class);
			final Iterator<PresetConfigurations> iteratorPresets = presetConfigurations.iterator();
			while (iteratorPresets.hasNext()) {
				final JsonObject configValue = new JsonObject();
				final PresetConfigurations next = iteratorPresets.next();
				for (String value : next.presetValues()) {
					final String[] values = value.split(":");
					configValue.addProperty(values[0], values[1]);
				}
				presetConfigs.add(next.presetKey(), configValue);
			}
            Internationalization internationalization = new Internationalization(pageResource.adaptTo(Page.class));
            String locale = internationalization.getLocale().toLowerCase();
            locale = locale.replace("-","_");
            internationalization.setDictionaryPath(pageResource.getPath() + "." + TranslationDictionaryServlet.TRANSLATION_CONFIGURATION_SELECTOR + "." + locale + ContentTypeUtil.EXT_JSON);

            final JsonObject socialMediaConfig = getSocialMediaConfiguration(pageResource);
           
            sessionAuth.setIdleTime(DsmUtils.getValueIfValid(globalConfiguration.idleTime(), "Idle timeout configuration"));
            sessionAuth.setRefreshTokenValidity(DsmUtils.getValueIfValid(globalConfiguration.refreshTokenValidity(), "Refresh token validity configuration"));
            sessionAuth.setAccessTokenValidity(DsmUtils.getValueIfValid(globalConfiguration.accessTokenValidity(), "Access token validity configuration"));
            sessionAuth.setSecureIdleTime(DsmUtils.getValueIfValid(globalConfiguration.secureIdleTime(), "Secure Idle timeout configuration"));
            sessionAuth.setDeleteInactiveMyFolderAfter(DsmUtils.getValueIfValid(globalConfiguration.deleteDaysAfterLastModification(), "Delete My Folder After Last Modification configuration"));
            LOGGER.info("secured page path are {}", securedPagePathConfiguration.securePagePathList().length);
            sessionAuth.setSecurePagePathList(securedPagePathConfiguration.securePagePathList());

            LOGGER.info("session auth is",sessionAuth.toString());
            LOGGER.info("session auth is {}",sessionAuth.toString());

            GlobalConfigBean globalConfigBean = new GlobalConfigBean();
            globalConfigBean.setPlpProducts(DsmUtils.getValueIfValid(globalConfiguration.plpProducts(), "PLP - Products Per Page"));
            globalConfigBean.setPriceUpdate(DsmUtils.getValueIfValid(globalConfiguration.priceUpdate(), "StandAlone Pricing"));
            globalConfigBean.setHomepageUrl(DsmUtils.getValueIfValid(globalConfiguration.homePageUrl(), "Homepage url configuration"));
            globalConfigBean.setSignInFragment(DsmUtils.getValueIfValid(globalConfiguration.signInFragment(), "Sign In Content Fragment Path"));
            globalConfigBean.setSignUpFragment(DsmUtils.getValueIfValid(globalConfiguration.signUpFragment(), "Sign Up Content Fragment Path"));
            globalConfigBean.setSearchPagePath(DsmUtils.getValueIfValid(globalConfiguration.searchPagePath(), "Search Page Path"));
            globalConfigBean.setAllStoresPagePath(DsmUtils.getValueIfValid(globalConfiguration.allStoresPagePath(), "All Stores Page Path"));
            globalConfigBean.setFindStorePagePath(DsmUtils.getValueIfValid(globalConfiguration.findStorePagePath(), "Find Store Page Path"));
            globalConfigBean.setPlpPagePath(DsmUtils.getValueIfValid(globalConfiguration.plpPagePath(), "PLP Page Path"));
            globalConfigBean.setCartLandingPagePath(DsmUtils.getValueIfValid(globalConfiguration.cartLandingPagePath(), "Cart Landing Page Path"));
            globalConfigBean.setStoreDetailsServletPath(DsmUtils.getValueIfValid(globalConfiguration.storeDetailsServletPath(), "Store Details Servlet Path"));
            globalConfigBean.setFindStoreJsonPath(DsmUtils.getValueIfValid(globalConfiguration.findStoreJsonPath(), "Find Store Json Path"));
            globalConfigBean.setBrowserStoreJsonPath(DsmUtils.getValueIfValid(globalConfiguration.browserStoreJsonPath(), "Browser Store Json Path"));
            globalConfigBean.setFindProResults(DsmUtils.getValueIfValid(globalConfiguration.findProResults(), "Find A Pro Content Fragment Path"));
           
            globalConfigBean.setFindaStorequery(DsmUtils.getValueIfValid(globalConfiguration.findaStoreQuery(), "Find A Store Query"));
            globalConfigBean.setBrandAccountTitle(DsmUtils.getValueIfValid(globalConfiguration.brandAccountTitle(), "Brands Account Title"));
            globalConfigBean.setRedirectMapServletPath(DsmUtils.getValueIfValid(globalConfiguration.redirectMapServletPath(), "Redirect Map Servlet Path"));
            globalConfigBean.setHostUrl(DsmUtils.getValueIfValid(globalConfiguration.hostUrl(), "Host Url"));

            globalConfigBean.setOrderDetailsPath(DsmUtils.getValueIfValid(globalConfiguration.orderDetailsPath(), "Order Details Path"));
            globalConfigBean.setOrderStatusPath(DsmUtils.getValueIfValid(globalConfiguration.orderStatusPath(), "Order Status Path"));
			globalConfigBean.setContinueShopPath(DsmUtils.getValueIfValid(globalConfiguration.continueShopPath(), "Continue Shop Path"));
            globalConfigBean.setOrderConfirmPath(DsmUtils.getValueIfValid(globalConfiguration.orderConfirmPath(), "Order Confirm Path"));
            globalConfigBean.setOrderHistoryPath(DsmUtils.getValueIfValid(globalConfiguration.orderHistoryPath(), "Order History Path"));
            
            globalConfigBean.setAccountSettingsPath(DsmUtils.getValueIfValid(globalConfiguration.accountSettingsPath(), "Account Setting Path"));
            globalConfigBean.setCheckoutPagePath(DsmUtils.getValueIfValid(globalConfiguration.checkoutPagePath(), "Checkout Page Path"));
            globalConfigBean.setApiEndPointServletPath(DsmUtils.getValueIfValid(globalConfiguration.apiEndPointServletPath(), "API EndPoint Servelt Path"));
            globalConfigBean.setApimHost(DsmUtils.getValueIfValid(globalConfiguration.apimHost(), "APIM Host"));
            globalConfigBean.setGenericListPath(DsmUtils.getValueIfValid(globalConfiguration.genericListPath(), "Generic List Path"));
            globalConfigBean.setSiteName(DsmUtils.getValueIfValid(globalConfiguration.siteName(), "Site Name"));
            globalConfigBean.setDmScene(DsmUtils.getValueIfValid(globalConfiguration.dmScene(), "DM Scene7"));
            globalConfigBean.setShareCartPagePath(DsmUtils.getValueIfValid(globalConfiguration.shareCartPagePath(), "Share Cart Page Path"));
            globalConfigBean.setMyFavoritesPagePath(DsmUtils.getValueIfValid(globalConfiguration.myFavoritesPagePath(), "My Favorites Page Path"));
            globalConfigBean.setFindingModelNumber(DsmUtils.getValueIfValid(globalConfiguration.findingModelNumber(), "Finding Model Number Servlet Path"));
            globalConfigBean.setSwatchUrl(DsmUtils.getValueIfValid(globalConfiguration.swatchUrl(), "Swatch Url"));
            globalConfigBean.setLegalPageListPath(DsmUtils.getValueIfValid(globalConfiguration.legalPageListPath(), "Legal Page Path"));
            globalConfigBean.setCompareJsonPath(DsmUtils.getValueIfValid(globalConfiguration.compareJsonPath(), "Compare Json Path"));
            
            globalConfigBean.setPlpTemplatePath(DsmUtils.getValueIfValid(globalConfiguration.plpTemplatePath(), "PLP Template Path"));
            globalConfigBean.setInspirationDetailPath(DsmUtils.getValueIfValid(globalConfiguration.inspirationDetailPath(), "Inspiration Detail Path"));
            globalConfigBean.setNewBadgeName(DsmUtils.getValueIfValid(globalConfiguration.newBadgeName(), "Name for the New Badge"));
            globalConfigBean.setSaleBadgeName(DsmUtils.getValueIfValid(globalConfiguration.saleBadgeName(), "Name for the Sale Badge"));
            globalConfigBean.setExclusiveBadgeName(DsmUtils.getValueIfValid(globalConfiguration.exclusiveBadgeName(), "Name for the Exclusive Badge"));
            globalConfigBean.setDiscontinuedBadgeName(DsmUtils.getValueIfValid(globalConfiguration.discontinuedBadgeName(), "Name for the Discontinued Badge"));
            globalConfigBean.setGeoLocationErrorMessage(DsmUtils.getValueIfValid(globalConfiguration.geoLocationErrorMessage(), "Geo Location Error Message"));
            globalConfigBean.setGeoLocationErrorTitle(DsmUtils.getValueIfValid(globalConfiguration.geoLocationErrorTitle(), "Geo Location Error Title"));
            globalConfigBean.setGeoLocationProErrorCtaLabel(DsmUtils.getValueIfValid(globalConfiguration.geoLocationProErrorCtaLabel(), "Geo Location Pro Error CTA Label"));
            globalConfigBean.setGeoLocationProErrorCtaLink(DsmUtils.getValueIfValid(globalConfiguration.geoLocationProErrorCtaLink(), "Geo Location Pro Error CTA Link"));
            globalConfigBean.setGeoLocationStoreErrorCtaLabel(DsmUtils.getValueIfValid(globalConfiguration.geoLocationStoreErrorCtaLabel(), "Geo Location Store Error CTA Label"));
            globalConfigBean.setGeoLocationStoreErrorCtaLink(DsmUtils.getValueIfValid(globalConfiguration.geoLocationStoreErrorCtaLink(), "Geo Location Store Error CTA Link"));
            //globalConfigBean.setPublish(apiEndPointOsgiConfigs.isPublish());
            globalConfigBean.setShortendPagePath(DsmUtils.getValueIfValid(globalConfiguration.shortendPagePath(), "Short End Page Path"));
            globalConfigBean.setDisableRecaptcha(globalConfiguration.isDisableRecaptcha());
            globalConfigBean.setIsEnableBrand(globalConfiguration.isEnableBrand());
            globalConfigBean.setIsCartOpenInNewTab(globalConfiguration.isCartOpenInNewTab());
            globalConfigBean.setIsPersonaANY(globalConfiguration.isPersonaANY());
            globalConfigBean.setServiceParts(globalConfiguration.serviceParts());
            global.setGlobalConfigBean(globalConfigBean);
            global.setInternationalization(internationalization);
            global.setRegex(regex);
            global.setRegexPattern(regexPattern);
            global.setAnnsacksPDPConfig(annsacksPDPConfig);
            global.setMarketParamValues(marketingParams);
            global.setApiEndPointData(apiEndPointData);
            global.setSocialMediaConfig(socialMediaConfig);            
            global.setSalesChannelConfig(salesChannelConfig);
            global.setPresetConfigs(presetConfigs);
            CommerceConfig commerce = new CommerceConfig();
            commerce.setSessionAuth(sessionAuth);
            global.setCommerce(commerce);
			
            globalConfigBean.setReadyToShip(
                    DsmUtils.getValueIfValid(globalConfiguration.readyToShip(), "Name for the Ready To Ship Badge"));
			globalConfigBean.setSpecialOrder(DsmUtils.getValueIfValid(globalConfiguration.specialOrder(),
                    "Name for the Special Order Badge"));
            globalConfigBean.setTargetPropertyID(globalConfiguration.targetPropertyID());
			globalConfigBean.setAdobeAnalytics(globalConfiguration.adobeAnalytics());
			globalConfigBean.setScene7AccountName(globalConfiguration.scene7AccountName());
			
			globalConfigBean.setScene7BaseUrl(globalConfiguration.scene7BaseUrl());
			
            globalConfigBean.setNeedHelpOrder(DsmUtils.getValueIfValid(globalConfiguration.needHelpOrder(), "Title for need help order"));
            globalConfigBean.setLwAppName(DsmUtils.getValueIfValid(globalConfiguration.lwAppName(), "LW App Name"));
            globalConfigBean.setMedalliaFormid(globalConfiguration.medalliaFormid());
            globalConfigBean.setNavigationHoverTiming(DsmUtils.getValueIfValid(globalConfiguration.navigationHoverTiming(), "Navigation V3 Hover Animation Timing"));
            globalConfigBean.setInstallationServicesPath(globalConfiguration.installationServicesPath());
            globalConfigBean.setProductRangePath(DsmUtils.getValueIfValid(globalConfiguration.productRangePath(), "Product Range List Path"));
            globalConfigBean.setLeadTimeDocPath(globalConfiguration.leadTimeDocPath());
            
            
        } catch (DsmFoundationalException | JsonException e) {
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.error("Exception occurred while fetching the global configuration", e);
        } catch (NullPointerException e) {
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.error("NullPointerException occurred while fetching the global configuration for path '{}'", pagePath, e);
        } finally {
            DsmUtils.closeResourceResolver(resourceResolver);
        }
    	
        out.print(new Gson().toJson(global));
    }
    
	private JsonObject getSocialMediaConfiguration(Resource pageResource) throws JsonException {
		final JsonObject socialMediaWrapper = new JsonObject();
		final JsonArray socialMediaConfigs = new JsonArray();
		final Collection<SocialMediaConfigurationList> socialMediaConfigurationList = pageResource.adaptTo(ConfigurationBuilder.class)
			.asCollection(SocialMediaConfigurationList.class);
		Iterator<SocialMediaConfigurationList> socialMediaConfigurationValues = socialMediaConfigurationList.iterator();
		while(socialMediaConfigurationValues.hasNext()) {
			final JsonObject socialMediaEntry = new JsonObject();
			final JsonObject socialMediaMap = new JsonObject();
			SocialMediaConfigurationList next = socialMediaConfigurationValues.next();
			socialMediaMap.addProperty("redirectUrl", next.redirectURL());  
			socialMediaMap.addProperty("name", next.socialMediaName());
			socialMediaMap.addProperty("iconPath", next.iconPath());
			socialMediaEntry.add("map", socialMediaMap);
			socialMediaConfigs.add(socialMediaEntry);
		}
		socialMediaWrapper.add("myArrayList", socialMediaConfigs);
		return socialMediaWrapper;
	}

    public static JsonObject getApiEndPointData(String  sitename) {
    	
    	JsonObject details = new JsonObject();
    	JsonObject map = new JsonObject();
    	try (ResourceResolver resolver = DsmUtils.getServiceResourceResolver(DsmConstants.CONFIGURATION_META_DATA_EDITOR_SERVICE)) {

			Resource pageResource;
			try {
				if(StringUtils.isNotEmpty(sitename)) {
					pageResource = DsmUtils.getPageResourceFromPath(sitename, resolver);
					Collection<APIEndPointConfigurationList> configs = pageResource.adaptTo(ConfigurationBuilder.class)
							.asCollection(APIEndPointConfigurationList.class);
					Iterator<APIEndPointConfigurationList> iterator = configs.iterator();
					
					while (iterator.hasNext()) {
						APIEndPointConfigurationList next = iterator.next();
						map.addProperty(next.endPointName(), next.endPointURL());
					}

				}
			} catch (DsmFoundationalException e) {
				LOGGER.error("Exception in getting the EndPoint configs", e.getMessage());
			} catch (JsonException e) {
				LOGGER.error("JSONException in getting the EndPoint configs", e.getMessage());
			}finally {
                DsmUtils.closeResourceResolver(resolver);
            }
		}
    	details.add("map", map);
    	return details;
    }
    
    

    
}
