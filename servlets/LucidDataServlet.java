package dsm.foundation.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
import javax.servlet.Servlet;

@Component(
        service = Servlet.class,
        property = {
            ServletResolverConstants.SLING_SERVLET_PATHS + "=" + "/bin/kohler/services/lwData",
            ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET })
@ServiceDescription("Lucid Data Servlet")
public class LucidDataServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(LucidDataServlet.class);

    private static final String CONTENT_TYPE_JSON = "application/json";

    private static final int DEFAULT_ROWS = 10;

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
        final String query = req.getParameter("q");
        final int rows = parsePositiveInt(req.getParameter("rows"), DEFAULT_ROWS);
        final JsonObject payload = new JsonObject();

        if (StringUtils.isBlank(query)) {
            payload.addProperty("status", "error");
            payload.addProperty("message", "Query parameter 'q' is required.");
            resp.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
        } else {
            try {
                final List<String> skus = LucidUtils.searchSkus(contextResource, query, rows);
                final JsonArray skuArray = new JsonArray();
                skus.forEach(skuArray::add);
                payload.addProperty("status", "success");
                payload.addProperty("query", query);
                payload.addProperty("count", skus.size());
                payload.add("skus", skuArray);
                resp.setStatus(SlingHttpServletResponse.SC_OK);
            } catch (DsmFoundationalException ex) {
                LOGGER.error("Lucid PLP search failed: {}", ex.getMessage(), ex);
                payload.addProperty("status", "error");
                payload.addProperty("message", ex.getMessage());
                resp.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        try (PrintWriter out = resp.getWriter()) {
            out.write(payload.toString());
        }
    }

    private static int parsePositiveInt(final String value, final int fallback) {
        if (StringUtils.isBlank(value)) {
            return fallback;
        }
        try {
            final int parsed = Integer.parseInt(value.trim());
            return parsed > 0 ? parsed : fallback;
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}

