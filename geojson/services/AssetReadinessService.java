package com.kallista.core.geojson.services;

import org.apache.sling.api.resource.ResourceResolver;

public interface AssetReadinessService {

    ReadinessResult check(String assetPath, ResourceResolver resolver);

    final class ReadinessResult {
        private final boolean ready;
        private final boolean assetMissing;
        private final String reason;

        private ReadinessResult(boolean ready, boolean assetMissing, String reason) {
            this.ready = ready;
            this.assetMissing = assetMissing;
            this.reason = reason;
        }

        public static ReadinessResult ready(String reason) {
            return new ReadinessResult(true, false, reason);
        }

        public static ReadinessResult notReady(String reason) {
            return new ReadinessResult(false, false, reason);
        }

        public static ReadinessResult missing(String reason) {
            return new ReadinessResult(false, true, reason);
        }

        public boolean isReady() {
            return ready;
        }

        public boolean isAssetMissing() {
            return assetMissing;
        }

        public String getReason() {
            return reason;
        }
    }
}
