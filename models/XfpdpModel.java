package dsm.foundation.core.models;

import java.util.List;
import java.util.Map;
import com.adobe.cq.export.json.ComponentExporter;


import dsm.foundation.core.models.FolderPathXfItem;



public interface XfpdpModel extends ComponentExporter {

    
    List<FolderPathXfItem> getFolderpathxf();
    

}