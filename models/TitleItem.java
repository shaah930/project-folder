package com.kallista.core.models;

import java.util.List;


public interface TitleItem {

    String getTabName();

    List<ColumnDetailsItem> getColumnDetails();

    List<ImageDetailsItem> getImageDetails();

}