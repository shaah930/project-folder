package com.kallista.core.geojson.services.impl;

import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.kallista.core.geojson.constants.GeoJsonPipelineConstants;
import com.kallista.core.geojson.services.PublishService;

@Designate(ocd = GeoJsonAssetServiceConfig.class)
@Component(service = PublishService.class)
public class PublishServiceImpl implements PublishService {

        private volatile String geoJsonPath = GeoJsonPipelineConstants.resolvePath(
            GeoJsonAssetServiceConfig.DEFAULT_DAM_BASE_PATH, GeoJsonPipelineConstants.GEOJSON_PATH);

    @Activate
    @Modified
    protected void activate(GeoJsonAssetServiceConfig config) {
        String basePath = config.damBasePath();
        if (basePath == null || basePath.trim().isEmpty()) {
            basePath = GeoJsonAssetServiceConfig.DEFAULT_DAM_BASE_PATH;
        }
        geoJsonPath = GeoJsonPipelineConstants.resolvePath(basePath, GeoJsonPipelineConstants.GEOJSON_PATH);
    }

    @Reference
    private Replicator replicator;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void publishGeoJson() {
        try (ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(
                java.util.Map.of(ResourceResolverFactory.SUBSERVICE, GeoJsonPipelineConstants.SUBSERVICE_NAME))) {
            publish(resolver, geoJsonPath);
        } catch (LoginException ex) {
            throw new IllegalStateException("Unable to obtain service resolver for GeoJSON publish", ex);
        }
    }

    @Override
    public void publish(ResourceResolver resolver, String assetPath) {
        Session session = resolver.adaptTo(Session.class);
        if (session == null) {
            throw new IllegalStateException("JCR session unavailable for publish");
        }
        try {
            replicator.replicate(session, ReplicationActionType.ACTIVATE, assetPath);
        } catch (ReplicationException ex) {
            throw new IllegalStateException("Unable to publish asset " + assetPath, ex);
        }
    }
}