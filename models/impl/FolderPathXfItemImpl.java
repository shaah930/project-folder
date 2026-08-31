package dsm.foundation.core.models.impl;

import dsm.foundation.core.models.FolderPathXfItem;




import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import com.adobe.cq.export.json.ExporterConstants;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.Exporter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.sling.models.annotations.Via;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       adapters = FolderPathXfItem.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, 
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class FolderPathXfItemImpl implements FolderPathXfItem {
    
    private static final Logger LOG = LoggerFactory.getLogger(FolderPathXfItemImpl.class);


    
    @ValueMapValue(name = "xf")
    private String xf;
    


    @PostConstruct
    private void init() {
        LOG.debug("Initializing FolderPathXfItemImpl");

        
        LOG.debug("xf: {}", xf);
        

    }


    
    @JsonProperty("xf")
    @Override
    public String getXf() {
        return xf;
    }
    


}