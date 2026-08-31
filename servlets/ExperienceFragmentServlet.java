package dsm.foundation.core.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dsm.foundation.core.constants.DsmConstants;
import dsm.foundation.core.exception.DsmFoundationalException;
import dsm.foundation.core.services.ContentService;
import dsm.foundation.core.utils.CommonUtil;

@Component(service = Servlet.class, property = {
		ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
		ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=" + DsmConstants.JSON,
		ServletResolverConstants.SLING_SERVLET_PATHS + DsmConstants.EXP_FRAGMENT_SERVLET_PATH })
@ServiceDescription("Experience fragments are crawled and returned")
public class ExperienceFragmentServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ExperienceFragmentServlet.class);

	private static final String EXPERIENCE_FRAGMENTS = "experience-fragments";
	private static final String RESOURCE_TYPE = "cq:PageContent";
	private static final String MISSING_PATH_ERROR_MSG = "The required parameter 'path' is missing. Example: path=/content/experience-fragments/your-site/us/en";
	private static final String FUTURE_DATE_ERROR_MSG = "The provided date is in the future. Please enter a valid past date.";
	private static final String INVALID_DATE_FORMAT_ERROR_MSG = "Invalid date format. Use YYYY-MM-DD.";

	@Reference
	private transient ContentService contentService;

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws IOException {
		ResourceResolver resolver = request.getResourceResolver();

		String path = trimToNull(request.getParameter("path"));
		if (path == null) {
			CommonUtil.sendBadRequestErrorResponse(response, MISSING_PATH_ERROR_MSG);
			return;
		}

		String lastIndexedDate = trimToNull(request.getParameter("lastIndexedDate"));
		boolean isPdpPath = path.contains("/pdp/");

		Map<String, String> predicates = new HashMap<>();
		predicates.put("path", path);
		predicates.put("type", RESOURCE_TYPE);
		predicates.put("p.offset", "0");
		predicates.put("p.limit", "-1");

		if (lastIndexedDate != null && !lastIndexedDate.isEmpty()) {
			try {
				LocalDate inputDate = LocalDate.parse(lastIndexedDate);
				if (inputDate.isAfter(LocalDate.now())) {
					CommonUtil.sendBadRequestErrorResponse(response, FUTURE_DATE_ERROR_MSG);
					return;
				}

				predicates.put("daterange.property", "cq:lastModified");
				predicates.put("daterange.lowerBound", inputDate + "T00:00:00.000+00:00");
				predicates.put("daterange.lowerOperation", ">");
			} catch (DateTimeParseException exception) {
				CommonUtil.sendBadRequestErrorResponse(response, INVALID_DATE_FORMAT_ERROR_MSG);
				return;
			}
		}

		try {
			JsonArray aemDocs = contentService.getAllContent(predicates, resolver, EXPERIENCE_FRAGMENTS);
			response.setContentType(DsmConstants.APPLICATION_JSON);
			response.setCharacterEncoding(DsmConstants.UTF);
			if (aemDocs == null || aemDocs.isEmpty()) {
				response.getWriter().write("[]");
				return;
			}

			if (isPdpPath) {
				JsonArray filteredArray = filterPdpPayload(aemDocs);
				response.getWriter().write(filteredArray.toString());
			} else {
				response.getWriter().write(aemDocs.toString());
			}

		} catch (DsmFoundationalException exception) {
			CommonUtil.sendErrorResponse(response, null);
			LOG.error("Exception occurred while getting " + "experience fragments from repository.", exception);
		}
	}

	private JsonArray filterPdpPayload(final JsonArray aemDocs) {

		JsonArray filteredArray = new JsonArray();
		for (JsonElement element : aemDocs) {
			try {
				JsonObject original = toJsonObject(element);
				if (original == null) {
					continue;
				}

				JsonObject filtered = new JsonObject();

				String[][] fields = { { "title", "title", "jcr:title" }, { "language", "language" },
						{ "brandValue", "brandValue" }, { "skuId", "skuId" },
						{ "page-description-content", "page-description-content" }, { "url", "url" },
						{ "cq:lastReplicated", "cq:lastReplicated" }, { "lastIndexedDate", "lastIndexedDate" },
						{ "keywords", "keywords" }, { "id", "id" } };

				for (String[] mapping : fields) {
					String outputKey = mapping[0];
					for (int i = 1; i < mapping.length; i++) {
						String inputKey = mapping[i];
						if (!original.has(inputKey) || original.get(inputKey).isJsonNull()) {
							continue;
						}

						JsonElement value = original.get(inputKey);
						if (value.isJsonArray()) {
							JsonArray array = value.getAsJsonArray();
							StringBuilder joinedValue = new StringBuilder();

							for (JsonElement item : array) {
								if (item.isJsonPrimitive()) {
									String itemValue = item.getAsString().trim();
									if (!itemValue.isEmpty()) {
										if (joinedValue.length() > 0) {
											joinedValue.append(" , ");
										}
										joinedValue.append(itemValue);
									}
								}
							}
							if (joinedValue.length() > 0) {
								filtered.addProperty(outputKey, joinedValue.toString());
								break;
							}

						} else if (value.isJsonPrimitive()) {
							String stringValue = value.getAsString().trim();
							if (!stringValue.isEmpty()) {
								filtered.addProperty(outputKey, stringValue);
								break;
							}
						}
					}
				}

				if (filtered.size() > 0) {
					filteredArray.add(filtered);
				}

			} catch (Exception exception) {
				LOG.error("Error processing experience fragment JSON object", exception);
			}
		}

		return filteredArray;
	}

	private JsonObject toJsonObject(final JsonElement element) {

		if (element == null || element.isJsonNull()) {
			return null;
		}

		if (element.isJsonObject()) {
			return element.getAsJsonObject();
		}

		return JsonParser.parseString(element.toString()).getAsJsonObject();
	}

	private String trimToNull(final String value) {

		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
