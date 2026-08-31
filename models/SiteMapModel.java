package com.kallista.core.models;

import java.util.List;

import com.adobe.cq.export.json.ComponentExporter;

public interface SiteMapModel extends ComponentExporter {
    List<SiteMapTitleItem> getTitle();

}