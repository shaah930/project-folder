package dsm.foundation.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dsm.foundation.core.constants.DsmConstants;
import dsm.foundation.core.services.SiteMapXmlGenerateService;

@Component(service = { Servlet.class }, property = {
		ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/khs/services/createsitemap",
		ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
		ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=" + DsmConstants.JSON, })
public class GenerateSitemapXMLServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 742022254090989114L;

	protected static final Logger LOG = LoggerFactory.getLogger(GenerateSitemapXMLServlet.class);

	@Reference
	SiteMapXmlGenerateService sitemapService;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		try {
			String sitePath = request.getParameter(DsmConstants.SITE_PATH);
			if (sitePath != null) {
				sitePath = sitePath.replaceAll("\\r\\n|\\r|\\n", " ");
				try {
					sitemapService.generateSiteMapXml(sitePath);
					response.setContentType(DsmConstants.APPLICATION_JSON);
					response.setCharacterEncoding(DsmConstants.UTF);
					response.getWriter().write("{\"status\":\"success\"}");
				} catch (Exception e) {
					LOG.error("Error while creating sitemap {}", e.getMessage());
					response.setStatus(500);
					response.setContentType(DsmConstants.APPLICATION_JSON);
					response.setCharacterEncoding(DsmConstants.UTF);
					response.getWriter().write("{\"status\":\"failure\",\"message\":\"check parameter values\"}");
				}

			} else {
				response.setStatus(400);
				response.setContentType(DsmConstants.APPLICATION_JSON);
				response.setCharacterEncoding(DsmConstants.UTF);
				response.getWriter().write("{\"status\":\"failure\",\"message\":\"missing required params\"}");
			}
		} catch (IOException e) {
			LOG.error("Error while creating sitemap {}", e.getMessage());
		}

	}
}