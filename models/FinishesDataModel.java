package com.kallista.core.models;
import java.util.List;
import com.adobe.cq.export.json.ComponentExporter;

public interface FinishesDataModel extends ComponentExporter{

    String getTitle();
	
	String getDescriptionText();

    List<FinishesTabItem> getFinishesTabItems();
}
