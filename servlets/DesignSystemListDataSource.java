package dsm.foundation.core.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Component;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;

import dsm.foundation.core.constants.DsmConstants;

@Component(service = Servlet.class, property = {
        "sling.servlet.resourceTypes=dsm/datasource/design-system-list",
        "sling.servlet.methods=GET"
})
public class DesignSystemListDataSource extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        ResourceResolver resourceResolver = request.getResourceResolver();

        // Path to the ACS Commons list
        String listPath = DsmConstants.AUTHORING_GUIDE_LINKS_LIST;
        Resource listResource = resourceResolver.getResource(listPath);

        List<Resource> options = new ArrayList<>();

        if (listResource != null) {
            // Iterate through list items
            Iterator<Resource> children = listResource.listChildren();
            while (children.hasNext()) {
                Resource child = children.next();
                ValueMap props = child.getValueMap();

                // Skip system properties
                if (child.getName().startsWith("jcr:") || 
                    child.getName().equals("rep:policy") ||
                    child.getName().equals("sling:resourceType")) {
                    continue;
                }

                // Get the value and text from the list item
                // Use 'value' property for dropdown value and 'jcr:title' or 'value' for display text
                String value = props.get("value", child.getName());
                String text = props.get("jcr:title", props.get("value", child.getName()));

                // Only add if we have a valid value
                if (value != null && !value.trim().isEmpty()) {
                    // Create option resource
                    ValueMap option = new ValueMapDecorator(new HashMap<>());
                    option.put("value", value);
                    option.put("text", text);

                    options.add(new ValueMapResource(resourceResolver, child.getPath(), "nt:unstructured", option));
                }
            }
        }

        // Create DataSource
        DataSource ds = options.isEmpty() ? EmptyDataSource.instance() : new SimpleDataSource(options.iterator());

        request.setAttribute(DataSource.class.getName(), ds);
    }
}