package com.kallista.core.geojson.services.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Kallista GeoJSON Asset Service", description = "GeoJSON pipeline settings")
public @interface GeoJsonAssetServiceConfig {

        String DEFAULT_DAM_BASE_PATH = "/content/dam/kallista-com";

    @AttributeDefinition(name = "DAM base path",
            description = "Base DAM path containing the CSV and GeoJSON folders")
        String damBasePath() default DEFAULT_DAM_BASE_PATH;

    @AttributeDefinition(name = "Archive retention count",
            description = "Maximum number of files retained in each CSV and GeoJSON archive")
    int archiveRetentionCount() default 10;
}