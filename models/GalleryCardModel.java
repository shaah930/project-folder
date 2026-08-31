package com.kallista.core.models;

import java.util.List;

import com.adobe.cq.export.json.ComponentExporter;

public interface GalleryCardModel extends ComponentExporter {

    List<GalleryCardTab> getTabs();
}
