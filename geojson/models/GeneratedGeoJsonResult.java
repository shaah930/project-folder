package com.kallista.core.geojson.models;

import java.nio.file.Path;

public class GeneratedGeoJsonResult {

    private final Path filePath;
    private final int featureCount;

    public GeneratedGeoJsonResult(Path filePath, int featureCount) {
        this.filePath = filePath;
        this.featureCount = featureCount;
    }

    public Path getFilePath() {
        return filePath;
    }

    public int getFeatureCount() {
        return featureCount;
    }
}