package dsm.foundation.core.servlets;

import com.adobe.granite.rest.Constants;
import com.day.cq.wcm.api.Page;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dsm.foundation.core.constants.DsmConstants;
import dsm.foundation.core.exception.DsmFoundationalException;
import dsm.foundation.core.pojo.Internationalization;
import dsm.foundation.core.utils.DsmUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.contentloader.ContentTypeUtil;
import org.apache.sling.servlets.post.JSONResponse;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Component(
        service = Servlet.class,
        property = {
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=" + ServletResolverConstants.DEFAULT_RESOURCE_TYPE,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=" + DsmConstants.JSON,
                ServletResolverConstants.SLING_SERVLET_SELECTORS + "=" + TranslationDictionaryServlet.TRANSLATION_CONFIGURATION_SELECTOR,
                ServletResolverConstants.SLING_SERVLET_SELECTORS + "=" + TranslationDictionaryServlet.TRANSLATION_CONFIGURATION_SELECTOR_LOCALE}
                )
public class TranslationDictionaryServlet extends SlingAllMethodsServlet {
    public static final String TRANSLATION_CONFIGURATION_SELECTOR = "i18nDictionary";
    public static final String TRANSLATION_CONFIGURATION_SELECTOR_LOCALE = "en_gb";
    protected static final Logger LOGGER = LoggerFactory.getLogger(TranslationDictionaryServlet.class);
    private static final long serialVersionUID = 2598426539166789516L;

    @Override
    protected void doGet(final SlingHttpServletRequest servletRequest, final SlingHttpServletResponse servletResponse)
            throws IOException {
        final PrintWriter out = servletResponse.getWriter();
        // Set response headers.
        servletResponse.setContentType(JSONResponse.RESPONSE_CONTENT_TYPE);
        servletResponse.setCharacterEncoding(Constants.DEFAULT_CHARSET);

        ResourceResolver resourceResolver = DsmUtils.getServiceResourceResolver(DsmConstants.CONFIGURATION_META_DATA_EDITOR_SERVICE);
        try {
            Resource pageResource = DsmUtils.getPageResourceFromPath(servletRequest.getResource().getPath(), resourceResolver);
            Internationalization internationalization = new Internationalization(pageResource.adaptTo(Page.class));
            String locale = internationalization.getLocale().toLowerCase();
            locale = locale.replace("-","_");
            String languageJsonPath = DsmConstants.I18 + locale + ContentTypeUtil.EXT_JSON;
            Resource translationResource = DsmUtils.getResourceFromPath(languageJsonPath, resourceResolver);

            if (translationResource == null) {
              LOGGER.error("Translation dictionary not found for path: {}", languageJsonPath);
              servletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
              return;
            }

            try (InputStream is = DsmUtils.getInputStreamByResource(translationResource);
                 Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                JsonElement element = new JsonParser().parse(reader);
                out.print(element.getAsJsonObject().toString());
            }

        } catch (DsmFoundationalException e) {
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.error("Exception occurred while fetching the translation configuration", e);
        } finally {
            DsmUtils.closeResourceResolver(resourceResolver);
        }
    }
}
