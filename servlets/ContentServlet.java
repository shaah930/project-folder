package dsm.foundation.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.json.JsonException;
import javax.servlet.Servlet;

import com.day.cq.dam.api.s7dam.utils.PublishUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dsm.foundation.core.utils.DsmUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
// import org.json.JSONArray;
// import org.json.JSONException;
// import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.NameConstants;

import dsm.foundation.core.constants.DsmConstants;
import dsm.foundation.core.exception.DsmFoundationalException;
import dsm.foundation.core.services.ContentService;
import dsm.foundation.core.utils.CommonUtil;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.servlet.http.HttpServletResponse;

@Component(service = Servlet.class, property = {
		ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
		ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=" + DsmConstants.JSON,
		ServletResolverConstants.SLING_SERVLET_PATHS + DsmConstants.CONTENT_SERVLET_PATH })
@ServiceDescription("AEM Pages are crawled and returned")
public class ContentServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = -734595725363361456L;

	protected static final Logger LOG = LoggerFactory.getLogger(ContentServlet.class);

	@Reference
	public transient ContentService contentService;

    @Reference
    private PublishUtils publishUtils;

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws IOException {

        ResourceResolver resourceResolver = request.getResourceResolver();
        String contentSource = "content";

        String path = request.getParameter("path");
        String ignoreProperty = request.getParameter("ignoreP");
        String resourceType = request.getParameter("resourceType");
        if(StringUtils.isEmpty(resourceType)){
            resourceType = "cq:PageContent";
        }
        //"2021-04-18"
        String lastIndexedDate = request.getParameter("lastIndexedDate");
        // Future Date
        if (StringUtils.isNotEmpty(lastIndexedDate)) {
            try {
                LocalDate inputDate = LocalDate.parse(lastIndexedDate);
                if (inputDate.isAfter(LocalDate.now())) {
                    sendJsonError(response, "The provided date is in the future. Please enter a valid past date.");
                    return;
                }
            } catch (DateTimeParseException e) {
                sendJsonError(response, "Invalid date format. Use YYYY-MM-DD.");
                return;
            }
        }

        if(StringUtils.isNotEmpty(path)) {
            Map<String, String> params = new HashMap<>();
            params.put("path", path);
            params.put("type", resourceType);
            if(lastIndexedDate != null){
                params.put("daterange.property",NameConstants.PN_PAGE_LAST_MOD);
                params.put("daterange.lowerBound",lastIndexedDate);
            }
            params.put("p.offset", "0");
            params.put("p.limit", "-1");
            if(!"true".equals(ignoreProperty)) {
                params.put("1_property", "isIndexable");
                params.put("1_property.value", "on");
            }
            try {
                JsonArray aemContents = contentService.getAllContent(params, resourceResolver, contentSource);
                if (aemContents == null || aemContents.size() == 0 || !isValidJsonObject(aemContents.get(0))) {
                    response.setCharacterEncoding(DsmConstants.UTF);
                    response.setContentType(DsmConstants.APPLICATION_JSON);
                    response.getWriter().write("[]");
                    return;
                }
                    JsonArray finalAemContents = getUpdatedJson(aemContents, request, response);

                    response.setCharacterEncoding(DsmConstants.UTF);
                    response.setContentType(DsmConstants.APPLICATION_JSON);
                    response.getWriter().write(finalAemContents.toString());
                }
             catch (DsmFoundationalException e) {
                CommonUtil.sendErrorResponse(response, null);
                LOG.error("Exception occurred while getting the content from AEM repository.", e);
            }
        } else {
            CommonUtil.sendErrorResponse(response, "The required parameter path missing, ex: path=/content/kohler");
        }
    }

	private void sendJsonError(SlingHttpServletResponse response, String message) throws IOException {
	    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");

	    try {
	        JsonObject errorJson = new JsonObject();
	        errorJson.addProperty("error", message != null ? message : "Unknown error occurred");

	        response.getWriter().write(errorJson.toString());
	    } catch (JsonException e) {
	        response.getWriter().write("{\"error\":\"JSON construction error\"}");
	    }
	}

	private boolean isValidJsonObject(Object obj) {
	    if (obj instanceof JsonObject) {
	        return true;
	    }
	    try {
	        //new JsonObject(obj.toString());
            JsonParser.parseString(obj.toString()).getAsJsonObject();
	        return true;
	    } catch (JsonException e) {
	        return false;
	    }
	}

	public JsonArray getUpdatedJson(JsonArray aemContents,SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        JsonArray finalAemContents = new JsonArray();

        for (int aemContent = 0; aemContent < aemContents.size(); aemContent++) {
            JsonObject jsonObject = null;
            try {
                //jsonObject = new JsonObject(aemContents.get(aemContent).toString());
               jsonObject =JsonParser.parseString(aemContents.get(aemContent).toString()).getAsJsonObject();

                HashMap<String, Object> aemJsonObjMap = new Gson().fromJson(String.valueOf(jsonObject), HashMap.class);
                //check if the map contains pageTitle
                if (aemJsonObjMap.keySet().contains("pageTitle")) {
                    String pageTile = (String) aemJsonObjMap.get("pageTitle");
                    //check if the pageTitle contains &amp; then replace with &
                    if (pageTile.contains("&amp;")) {
                        pageTile = pageTile.replaceAll("&amp;", "&");
                        aemJsonObjMap.put("pageTitle", pageTile);
                    }
                }
                if(aemJsonObjMap.keySet().contains("templateName")) {
                    String templateName = (String) aemJsonObjMap.get("templateName");
                if( templateName.equals("Shop The Room")){
                    if(aemJsonObjMap.keySet().contains("landingFileReference")){
                        String fileReference = (String) aemJsonObjMap.get("landingFileReference");
                        aemJsonObjMap.put("landingFileReference", DsmUtils.getImageLink(fileReference,request.getResourceResolver(),publishUtils));
                    }
                    if(aemJsonObjMap.keySet().contains("modelFileReference")){
                        String fileReference = (String) aemJsonObjMap.get("modelFileReference");
                        aemJsonObjMap.put("modelFileReference", DsmUtils.getImageLink(fileReference,request.getResourceResolver(),publishUtils));
                    }
                }
                }
                //finalAemContents.put(aemJsonObjMap);
                finalAemContents.add(new Gson().toJsonTree(aemJsonObjMap));
            } catch (JsonException e) {
                CommonUtil.sendErrorResponse(response, null);
                LOG.error("Exception occurred while converting JSOn Array to JSON Object", e);
            }catch (RepositoryException e) {
                CommonUtil.sendErrorResponse(response, null);
                LOG.error("Exception occurred while fetching imageserverUrl", e);            }

        }
        return finalAemContents;
       
    }
}
