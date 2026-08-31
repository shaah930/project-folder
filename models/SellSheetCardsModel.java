package com.kallista.core.models;

import java.util.List;

import com.adobe.cq.export.json.ComponentExporter;

public interface SellSheetCardsModel extends ComponentExporter {

    String getTitle();

    String getLabel();

    String getLink();

    List<SellSheetCardItem> getSellSheetCards();
}