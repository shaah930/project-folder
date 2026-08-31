package dsm.foundation.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dsm.foundation.core.exception.DsmFoundationalException;
import dsm.foundation.core.utils.LucidUtils;

/**
 * Servlet exposing PLP category / attribute lookups for the PLP page dialog.
 *
 * Supported actions (controlled by the {@code action} query parameter):
 * <ul>
 *   <li>{@code categories} - returns tag + Lucid profile pairs from the
 *       {@code profile_<lwAppName>_Rules} profile.</li>
 *   <li>{@code attributeNames} - requires {@code profile}; returns the
 *       {@code fusion.facet_labels} entries for that profile, with any key
 *       containing {@code price}/{@code pricelist} filtered out.</li>
 *   <li>{@code attributeValues} - requires {@code profile} and {@code attribute};
 *       returns the distinct values for that facet field.</li>
 * </ul>
 */
@Component(
        service = Servlet.class,
        property = {
            ServletResolverConstants.SLING_SERVLET_PATHS + "=" + "/bin/kohler/services/lwPlpData",
            ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET })
@ServiceDescription("Lucid PLP Data Servlet")
public class LucidPLPDataServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(LucidPLPDataServlet.class);

    private static final String CONTENT_TYPE_JSON = "application/json";

    private static final String ACTION_CATEGORIES = "categories";
    private static final String ACTION_ATTRIBUTE_NAMES = "attributeNames";
    private static final String ACTION_ATTRIBUTE_VALUES = "attributeValues";

    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws IOException {

        resp.setContentType(CONTENT_TYPE_JSON);
        resp.setCharacterEncoding("UTF-8");

        final String contextPath = req.getParameter("path");
        Resource contextResource = null;
        if (StringUtils.isNotBlank(contextPath)) {
            contextResource = req.getResourceResolver().getResource(contextPath);
            if (contextResource == null) {
                LOGGER.warn("Configuration context resource not found at path {}.", contextPath);
            }
        }
        if (contextResource == null) {
            contextResource = req.getResource();
        }

        final String action = req.getParameter("action");
        final JsonObject payload = new JsonObject();

        try {
            if (StringUtils.isBlank(action)) {
                payload.addProperty("status", "error");
                payload.addProperty("message", "Parameter 'action' is required.");
                resp.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            } else {
                switch (action) {
                    case ACTION_CATEGORIES:
                        handleCategories(contextResource, payload);
                        resp.setStatus(SlingHttpServletResponse.SC_OK);
                        break;
                    case ACTION_ATTRIBUTE_NAMES:
                        handleAttributeNames(req, contextResource, payload, resp);
                        break;
                    case ACTION_ATTRIBUTE_VALUES:
                        handleAttributeValues(req, contextResource, payload, resp);
                        break;
                    default:
                        payload.addProperty("status", "error");
                        payload.addProperty("message", "Unsupported action '" + action + "'.");
                        resp.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
                        break;
                }
            }
        } catch (DsmFoundationalException ex) {
            LOGGER.error("Lucid PLP data request failed: {}", ex.getMessage(), ex);
            payload.entrySet().clear();
            payload.addProperty("status", "error");
            payload.addProperty("message", ex.getMessage());
            resp.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        try (PrintWriter out = resp.getWriter()) {
            out.write(payload.toString());
        }
    }

    private void handleCategories(final Resource contextResource, final JsonObject payload)
            throws DsmFoundationalException {
        final List<Map<String, String>> profiles = LucidUtils.getProfiles(contextResource);
        final JsonArray array = new JsonArray();
        for (Map<String, String> entry : profiles) {
            final JsonObject obj = new JsonObject();
            obj.addProperty("tag", entry.get("tag"));
            obj.addProperty("profile", entry.get("profile"));
            array.add(obj);
        }
        payload.addProperty("status", "success");
        payload.addProperty("count", profiles.size());
        payload.add("categories", array);
    }

    private void handleAttributeNames(final SlingHttpServletRequest req, final Resource contextResource,
            final JsonObject payload, final SlingHttpServletResponse resp) throws DsmFoundationalException {
        final String profile = req.getParameter("profile");
        if (StringUtils.isBlank(profile)) {
            payload.addProperty("status", "error");
            payload.addProperty("message", "Parameter 'profile' is required.");
            resp.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final Map<String, String> attributes = LucidUtils.getAttributeNames(contextResource, profile);
        final JsonArray array = new JsonArray();
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            final JsonObject obj = new JsonObject();
            obj.addProperty("key", entry.getKey());
            obj.addProperty("label", entry.getValue());
            array.add(obj);
        }
        payload.addProperty("status", "success");
        payload.addProperty("profile", profile);
        payload.addProperty("count", attributes.size());
        payload.add("attributes", array);
        resp.setStatus(SlingHttpServletResponse.SC_OK);
    }

    private void handleAttributeValues(final SlingHttpServletRequest req, final Resource contextResource,
            final JsonObject payload, final SlingHttpServletResponse resp) throws DsmFoundationalException {
        final String profile = req.getParameter("profile");
        final String attribute = req.getParameter("attribute");
        if (StringUtils.isBlank(profile) || StringUtils.isBlank(attribute)) {
            payload.addProperty("status", "error");
            payload.addProperty("message", "Parameters 'profile' and 'attribute' are required.");
            resp.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final List<String> values = LucidUtils.getAttributeValues(contextResource, profile, attribute);
        final JsonArray array = new JsonArray();
        values.forEach(array::add);
        payload.addProperty("status", "success");
        payload.addProperty("profile", profile);
        payload.addProperty("attribute", attribute);
        payload.addProperty("count", values.size());
        payload.add("values", array);
        resp.setStatus(SlingHttpServletResponse.SC_OK);
    }
}
