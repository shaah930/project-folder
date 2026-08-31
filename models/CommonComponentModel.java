package dsm.foundation.core.models;

import java.util.*;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.day.cq.wcm.api.Page;

import dsm.foundation.core.caconfig.DataLayerConfigurationList;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.ConfigurationBuilder;

/**
 * A Common class to contain the common methods for the component datalayer
 */
public class CommonComponentModel {
    public static final String ID_SEPARATOR = "-";
    protected static final List<String> urlPatterns = Arrays.asList(".*/[^/]*?/orders",".*/[^/]*?/services",".*/[^/]*?/legal",".*/[^/]*?/about-us",".*/[^/]*?/my-account",".*/[^/]*?/locations$",".*/[^/]*?/support",".*/[^/]*?/cart");
    protected static final List<String> eCommerceUrlPatterns = Arrays.asList(".*/[^/]*?/cart",".*/[^/]*?/checkout",".*/[^/]*?/order-confirmation");
    protected static final List<String> specialServiceName = Arrays.asList("bathroom-design-services","installation-services","find-a-pro-results","environmental-product-declaration");
    public static final String CQ_PANEL_TITLE = "cq:panelTitle";
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

    /**
     * Fetch the pageType/Category from the URL pattern or the configuration
     * based on template.
     *
     * @return
     */
    public String getCurrentPageType(Page currentPage, SlingHttpServletRequest request){
        if(currentPage.getLanguage() != null && currentPage.getLanguage().getLanguage() != null && currentPage.getName().equalsIgnoreCase(currentPage.getLanguage().getLanguage().toLowerCase())){
            return "homepage";
        }
        if(specialServiceName.contains(currentPage.getName())){
            return currentPage.getName().replaceAll("-"," ").toLowerCase();
        }
        String pageType = getPageTypeBasedOnUrl(currentPage);
        if (pageType != null) return pageType.toLowerCase();

        Collection<DataLayerConfigurationList> configs = request.getResource().adaptTo(ConfigurationBuilder.class)
                .asCollection(DataLayerConfigurationList.class);
        Iterator<DataLayerConfigurationList> iterator = configs.iterator();
        while (iterator.hasNext()) {
            DataLayerConfigurationList next = iterator.next();
            if(currentPage.getTemplate() != null && currentPage.getTemplate().getName().equalsIgnoreCase(next.templateName())){
                return next.pageName();
            }
        }
        return currentPage.getName().replaceAll("-", " ").toLowerCase();
    }

    protected String getPageTypeBasedOnUrl(Page currentPage) {
        //First check with the configured URL before resolving the config.
        String pagePath = currentPage.getPath();
        for(String regex : urlPatterns){
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(pagePath);
            boolean matchFound = matcher.find();
            int pageTypeindex = pagePath.indexOf(StringUtils.substringAfterLast(regex.replace("$",""), "/"));
            if( matchFound && pageTypeindex != -1) {
            	String pageType = pagePath.substring(pageTypeindex).replace("/", ":").replace("-", " ");
                return pageType;
            }
        }
        return null;
    }

    public String cleanString(String str) {
        return str!=null ? str.replaceAll("\\r\\n|\\r|\\n", " ").replaceAll("<.*?>|®|™", "").toLowerCase().trim() : StringUtils.EMPTY;
    }


    /**
     * Return the Datalayer Link type for  Store details page
     *
     * @param currentPage
     * @return
     */
     public String getDataLayerLinkType(Page currentPage) {
        StringBuilder storePageType = new StringBuilder("");
        if(currentPage.getTemplate() != null && "store-detail-page".equalsIgnoreCase(currentPage.getTemplate().getName())){
            //This is store detail page
            storePageType.append("find a store:");
            storePageType.append(currentPage.getName().replaceAll("-", " "));
            storePageType.append(":").append(currentPage.getParent().getName().replaceAll("-"," "));
            storePageType.append(":").append(currentPage.getParent(2).getName().replaceAll("-", ""));
        }
        return storePageType.toString();
    }

}