package com.kallista.core.geojson.services.impl;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;

import com.day.cq.dam.api.Asset;
import com.kallista.core.geojson.services.AssetReadinessService;

@Component(service = AssetReadinessService.class)
public class DamAssetReadinessServiceImpl implements AssetReadinessService {

    private static final String DAM_ASSET_STATE = "dam:assetState";
    private static final String STATE_PROCESSING = "processing";
    private static final String STATE_UNPROCESSED = "unprocessed";

    @Override
    public ReadinessResult check(String assetPath, ResourceResolver resolver) {
        Resource assetResource = resolver.getResource(assetPath);
        if (assetResource == null) {
            return ReadinessResult.missing("asset path does not exist");
        }

        Asset asset = assetResource.adaptTo(Asset.class);
        if (asset == null) {
            return ReadinessResult.notReady("resource is not a DAM asset");
        }

        Resource jcrContent = assetResource.getChild("jcr:content");
        if (jcrContent == null) {
            return ReadinessResult.notReady("jcr:content node is missing");
        }

        Resource metadata = assetResource.getChild("jcr:content/metadata");
        if (metadata == null) {
            return ReadinessResult.notReady("metadata node is missing");
        }

        Resource originalBinary = assetResource.getChild("jcr:content/renditions/original/jcr:content");
        if (originalBinary == null) {
            return ReadinessResult.notReady("original rendition content is missing");
        }

        ValueMap originalMap = originalBinary.getValueMap();
        if (originalMap.get("jcr:data") == null) {
            return ReadinessResult.notReady("original rendition binary is not yet available");
        }

        String assetState = jcrContent.getValueMap().get(DAM_ASSET_STATE, String.class);
        if (assetState != null) {
            String normalized = assetState.trim().toLowerCase();
            if (STATE_UNPROCESSED.equals(normalized) || STATE_PROCESSING.equals(normalized)) {
                return ReadinessResult.notReady("asset state is " + assetState);
            }
            return ReadinessResult.ready("asset state is " + assetState);
        }

        return ReadinessResult.ready("asset state not set; original rendition and metadata are present");
    }
}
