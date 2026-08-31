package dsm.foundation.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.Page;

import javax.jcr.RepositoryException;
import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface PageComponentModel extends ComponentExporter {    
	String getExportedType();
	String getFooterExpFrg();
	String getHeaderExpFrg();
    String getTopHeadScripts();
	String getBottomHeadScripts();
	String getBodyStartScripts();
	String getFooterScripts();
	String getSeoSchema();
	Page getPageComponentProperties();
	String getData() throws RepositoryException;
	String getEventPageType();	
	 String getSeoPageScript();
	 String getGlobalScript();
	String getAdobeAnalytic();
	 String getSchemaJsonLD();    
	 String getSchemaData(String schemaType);
}

