package dsm.foundation.core.models.impl;

import dsm.foundation.core.models.XfpdpModel;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;  
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.Exporter;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.sling.models.annotations.Via;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import javax.inject.Inject;


import dsm.foundation.core.models.FolderPathXfItem;



@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
       adapters = { XfpdpModel.class,ComponentExporter.class},
       resourceType = XfpdpModelImpl.RESOURCE_TYPE,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class XfpdpModelImpl implements XfpdpModel {

    private static final Logger LOG = LoggerFactory.getLogger(XfpdpModelImpl.class);
    static final String RESOURCE_TYPE = "aem-dsm-foundation/components/xfPDP/v1/xfPDP";


    
    @Inject
    @Via("resource")
    private List<FolderPathXfItem> folderPathXF;
    


    @PostConstruct
    private void init() {
        LOG.debug("Initializing XfpdpModelImpl");

    
        LOG.debug("folderPathXF nested multifield size: {}", folderPathXF != null ? folderPathXF.size() : "null");
    

    }


    
    @JsonProperty("folderPathXF")
    @Override
    public List<FolderPathXfItem> getFolderpathxf() {
        return folderPathXF != null ? folderPathXF : Collections.emptyList();
    }
    


    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }
}