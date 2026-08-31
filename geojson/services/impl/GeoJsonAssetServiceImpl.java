package com.kallista.core.geojson.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.kallista.core.geojson.constants.GeoJsonPipelineConstants;
import com.kallista.core.geojson.listener.CsvUploadResourceChangeListener;
import com.kallista.core.geojson.services.GeoJsonAssetService;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

@Designate(ocd = GeoJsonAssetServiceConfig.class)
@Component(service = GeoJsonAssetService.class)
public class GeoJsonAssetServiceImpl implements GeoJsonAssetService {
 private static final Logger LOG = LoggerFactory.getLogger(GeoJsonAssetServiceImpl.class);

    private static final DateTimeFormatter ARCHIVE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private volatile int archiveRetentionCount = 10;
        private volatile String csvArchivePath = GeoJsonPipelineConstants.resolvePath(
            GeoJsonAssetServiceConfig.DEFAULT_DAM_BASE_PATH, GeoJsonPipelineConstants.CSV_ARCHIVE_PATH);
        private volatile String geoJsonPath = GeoJsonPipelineConstants.resolvePath(
            GeoJsonAssetServiceConfig.DEFAULT_DAM_BASE_PATH, GeoJsonPipelineConstants.GEOJSON_PATH);
        private volatile String geoJsonArchivePath = GeoJsonPipelineConstants.resolvePath(
            GeoJsonAssetServiceConfig.DEFAULT_DAM_BASE_PATH, GeoJsonPipelineConstants.GEOJSON_ARCHIVE_PATH);

    @org.osgi.service.component.annotations.Activate
    @org.osgi.service.component.annotations.Modified
    protected void activate(GeoJsonAssetServiceConfig config) {
        archiveRetentionCount = Math.max(0, config.archiveRetentionCount());
        String basePath = normalizeBasePath(config.damBasePath());
        csvArchivePath = GeoJsonPipelineConstants.resolvePath(basePath, GeoJsonPipelineConstants.CSV_ARCHIVE_PATH);
        geoJsonPath = GeoJsonPipelineConstants.resolvePath(basePath, GeoJsonPipelineConstants.GEOJSON_PATH);
        geoJsonArchivePath = GeoJsonPipelineConstants.resolvePath(basePath, GeoJsonPipelineConstants.GEOJSON_ARCHIVE_PATH);
    }

    @Override
    public InputStream openOriginalBinary(String assetPath) throws IOException {
        try {
            ResourceResolver resolver = getServiceResolver();
            Resource assetResource = resolver.getResource(assetPath);
            if (assetResource == null) {
                resolver.close();
                throw new IOException("Asset not found: " + assetPath);
            }
            Asset asset = assetResource.adaptTo(Asset.class);
            if (asset == null) {
                resolver.close();
                throw new IOException("Resource is not an asset: " + assetPath);
            }
            return new ResolverBackedInputStream(resolver, asset.getOriginal().getStream());
        } catch (Exception ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            }
            throw new IOException("Unable to open original binary for " + assetPath, ex);
        }
    }

    @Override
    public void backupExistingGeoJson() {
        try (ResourceResolver resolver = getServiceResolver()) {
            Resource existing = resolver.getResource(geoJsonPath);
            if (existing == null) {
                return;
            }
            ensureArchivePathExistsWithoutCommit(resolver, geoJsonArchivePath);
            resolver.commit();  // Commit folder creations before move

            String archivePath = buildArchivePath(geoJsonArchivePath, "stores", "geojson");
            Session session = resolver.adaptTo(Session.class);
            if (session == null) {
                throw new IllegalStateException("Unable to adapt ResourceResolver to JCR Session");
            }

            session.move(geoJsonPath, archivePath);
            session.save();
        cleanupArchive(resolver, geoJsonArchivePath);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to backup existing GeoJSON asset", ex);
        }
    }

    @Override
    public void saveGeoJson(InputStream inputStream) throws IOException {
        try (ResourceResolver resolver = getServiceResolver()) {
            AssetManager assetManager = resolver.adaptTo(AssetManager.class);
            if (assetManager == null) {
                throw new IOException("AssetManager is unavailable for geojson save");
            }
            assetManager.createAsset(geoJsonPath, inputStream, "application/geo+json", true);
        } catch (Exception ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            }
            throw new IOException("Unable to save GeoJSON", ex);
        }
    }

    @Override
    public void archiveCsvAsset(String assetPath) {
        try (ResourceResolver resolver = getServiceResolver()) {
            Resource assetResource = resolver.getResource(assetPath);
            if (assetResource == null) {
                throw new IllegalStateException("CSV asset not found: " + assetPath);
            }
            Asset asset = assetResource.adaptTo(Asset.class);
            if (asset == null) {
                throw new IllegalStateException("CSV resource is not an asset: " + assetPath);
            }
            archiveCsv(resolver, asset);
        } catch (Exception ex) {
            throw new IllegalStateException("archiveCsvAsset :: Unable to archive CSV asset " + assetPath, ex);
        }
    }

    @Override
    public String saveGeoJson(ResourceResolver resolver, Path geoJsonFile) throws IOException {
        AssetManager assetManager = resolver.adaptTo(AssetManager.class);
        if (assetManager == null) {
            throw new IllegalStateException("AssetManager is unavailable for geojson save");
        }

        Resource existing = resolver.getResource(geoJsonPath);
        if (existing != null) {
            try {
                ensureArchivePathExistsWithoutCommit(resolver, geoJsonArchivePath);
                resolver.commit();  // Commit folder creations before move
                String archivePath = buildArchivePath(geoJsonArchivePath, "stores", "geojson");

                Session session = resolver.adaptTo(Session.class);
                if (session == null) {
                    throw new IllegalStateException("Unable to adapt ResourceResolver to JCR Session");
                }
                session.move(geoJsonPath, archivePath);
                session.save();
                cleanupArchive(resolver, geoJsonArchivePath);
            } catch (RepositoryException |PersistenceException ex) {
                throw new IOException("Unable to backup existing GeoJSON", ex);
            }
        }

        try (InputStream inputStream = Files.newInputStream(geoJsonFile)) {
            assetManager.createAsset(geoJsonPath, inputStream, "application/geo+json", true);
        }
        return geoJsonPath;
    }

    @Override
    public String archiveCsv(ResourceResolver resolver, Asset csvAsset) {
        AssetManager assetManager = resolver.adaptTo(AssetManager.class);
        if (assetManager == null) {
            throw new IllegalStateException("AssetManager is unavailable for csv archive");
        }

        String originalPath = csvAsset.getPath();
        String originalName = csvAsset.getName();
        String baseName = originalName.endsWith(".csv") ? originalName.substring(0, originalName.length() - 4) : originalName;
        try {
            ensureArchivePathExistsWithoutCommit(resolver, csvArchivePath);
            resolver.commit(); // Commit the creation of the archive path before moving the asset
            String archivePath = buildArchivePath(csvArchivePath, baseName, "csv");

            Session session = resolver.adaptTo(Session.class);
            if (session == null) {
                throw new IllegalStateException("Unable to adapt ResourceResolver to JCR Session");
            }

            session.move(originalPath, archivePath);
            session.save();
            cleanupArchive(resolver, csvArchivePath);
            return archivePath;
        } catch (RepositoryException |PersistenceException ex) {
            LOG.error("session implementation ex message ={}",ex.getMessage());
            throw new IllegalStateException("archiveCsv :: Unable to archive CSV asset " + originalPath, ex);
        }
    }

    private static String normalizeBasePath(String basePath) {
        String value = basePath == null || basePath.trim().isEmpty()
                ? GeoJsonAssetServiceConfig.DEFAULT_DAM_BASE_PATH : basePath.trim();
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private static String buildArchivePath(String archiveRoot, String baseName, String extension) {
        return archiveRoot + '/' + baseName + '_' + LocalDateTime.now().format(ARCHIVE_FORMATTER) + '.' + extension;
    }

    void cleanupArchive(ResourceResolver resolver, String archivePath) throws PersistenceException {
        Resource archive = resolver.getResource(archivePath);
        if (archive == null) {
            return;
        }

        List<Resource> archivedAssets = new ArrayList<>();
        Iterator<Resource> children = archive.listChildren();
        while (children.hasNext()) {
            Resource child = children.next();
            if (child.adaptTo(Asset.class) != null) {
                archivedAssets.add(child);
            }
        }

        if (archivedAssets.size() <= archiveRetentionCount) {
            return;
        }

        archivedAssets.sort(Comparator.comparingLong(this::modificationTime)
                .thenComparing(Resource::getPath));
        int deleteCount = archivedAssets.size() - archiveRetentionCount;
        for (int index = 0; index < deleteCount; index++) {
            Resource archivedAsset = archivedAssets.get(index);
            try {
                resolver.delete(archivedAsset);
                LOG.info("Deleted archived asset {} because archive retention count is {}", archivedAsset.getPath(),
                        archiveRetentionCount);
            } catch (PersistenceException ex) {
                LOG.error("Unable to delete archived asset {}", archivedAsset.getPath(), ex);
            }
        }
        resolver.commit();
    }

    private long modificationTime(Resource resource) {
        long modificationTime = resource.getResourceMetadata().getModificationTime();
        return modificationTime >= 0 ? modificationTime : Long.MIN_VALUE;
    }

    private void ensureArchivePathExistsWithoutCommit(ResourceResolver resolver, String archivePath) throws PersistenceException {
        Resource archiveResource = resolver.getResource(archivePath);
        if (archiveResource != null) {
            return;
        }
        
        // Create the archive directory structure
        String[] pathSegments = archivePath.split("/");
        StringBuilder currentPath = new StringBuilder();
        java.util.Map<String, Object> properties = new java.util.HashMap<>();
        properties.put("jcr:primaryType", "sling:Folder");
        
        for (String segment : pathSegments) {
            if (segment.isEmpty()) {
                continue;
            }
            currentPath.append('/').append(segment);
            String currentPathStr = currentPath.toString();
            Resource currentResource = resolver.getResource(currentPathStr);
            if (currentResource == null) {
                String parentPath = currentPathStr.substring(0, currentPathStr.lastIndexOf('/'));
                Resource parentResource;
                if (parentPath.isEmpty() || parentPath.equals("")) {
                    parentResource = resolver.getResource("/");
                } else {
                    parentResource = resolver.getResource(parentPath);
                }
                
                if (parentResource == null) {
                    throw new PersistenceException("Cannot create path '" + currentPathStr + "': parent '" + parentPath + "' does not exist");
                }
                
                try {
                    resolver.create(parentResource, segment, properties);
                  
                } catch (Exception ex) {
                    throw new PersistenceException("Failed to create folder '" + segment + "' under '" + parentPath + "'", ex);
                }
            }
        }
    }


    private ResourceResolver getServiceResolver() throws org.apache.sling.api.resource.LoginException {
        return resourceResolverFactory.getServiceResourceResolver(
                java.util.Map.of(ResourceResolverFactory.SUBSERVICE, GeoJsonPipelineConstants.SUBSERVICE_NAME));
    }

    private static final class ResolverBackedInputStream extends InputStream {

        private final ResourceResolver resolver;
        private final InputStream delegate;

        private ResolverBackedInputStream(ResourceResolver resolver, InputStream delegate) {
            this.resolver = resolver;
            this.delegate = delegate;
        }

        @Override
        public int read() throws IOException {
            return delegate.read();
        }

        @Override
        public int read(byte[] buffer) throws IOException {
            return delegate.read(buffer);
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            return delegate.read(buffer, offset, length);
        }

        @Override
        public void close() throws IOException {
            try {
                delegate.close();
            } finally {
                resolver.close();
            }
        }
    }
}