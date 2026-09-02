package ee.matteus.plaanisepp.core.service;

import ee.matteus.plaanisepp.core.map.BaseMapBounds;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public final class PlanSnapshot {
    private final Map<String, String> properties;
    private final MapImageAsset mapImageAsset;
    private final BaseMapAsset baseMapAsset;

    PlanSnapshot(Map<String, String> properties, MapImageAsset mapImageAsset, BaseMapAsset baseMapAsset) {
        this.properties = Map.copyOf(properties);
        this.mapImageAsset = mapImageAsset;
        this.baseMapAsset = baseMapAsset;
    }

    Map<String, String> properties() {
        return properties;
    }

    MapImageAsset mapImageAsset() {
        return mapImageAsset;
    }

    BaseMapAsset baseMapAsset() {
        return baseMapAsset;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PlanSnapshot snapshot)) {
            return false;
        }
        return properties.equals(snapshot.properties)
                && Objects.equals(mapImageAsset, snapshot.mapImageAsset)
                && Objects.equals(baseMapAsset, snapshot.baseMapAsset);
    }

    @Override
    public int hashCode() {
        return 31 * (31 * properties.hashCode() + Objects.hashCode(mapImageAsset))
                + Objects.hashCode(baseMapAsset);
    }

    static final class MapImageAsset {
        private final String entryName;
        private final byte[] data;

        MapImageAsset(String entryName, byte[] data) {
            this.entryName = entryName;
            this.data = data;
        }

        String entryName() {
            return entryName;
        }

        byte[] data() {
            return data;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof MapImageAsset asset)) {
                return false;
            }
            return entryName.equals(asset.entryName) && Arrays.equals(data, asset.data);
        }

        @Override
        public int hashCode() {
            return 31 * entryName.hashCode() + Arrays.hashCode(data);
        }
    }

    static final class BaseMapAsset {
        private final byte[] regularMap;
        private final byte[] orthophoto;
        private final BaseMapBounds bounds;
        private final boolean orthophotoActive;

        BaseMapAsset(byte[] regularMap, byte[] orthophoto, BaseMapBounds bounds, boolean orthophotoActive) {
            this.regularMap = regularMap;
            this.orthophoto = orthophoto;
            this.bounds = bounds;
            this.orthophotoActive = orthophotoActive;
        }

        byte[] regularMap() { return regularMap; }
        byte[] orthophoto() { return orthophoto; }
        BaseMapBounds bounds() { return bounds; }
        boolean orthophotoActive() { return orthophotoActive; }

        @Override
        public boolean equals(Object other) {
            return other instanceof BaseMapAsset asset
                    && Arrays.equals(regularMap, asset.regularMap)
                    && Arrays.equals(orthophoto, asset.orthophoto)
                    && bounds.equals(asset.bounds)
                    && orthophotoActive == asset.orthophotoActive;
        }

        @Override
        public int hashCode() {
            int result = 31 * Arrays.hashCode(regularMap) + Arrays.hashCode(orthophoto);
            result = 31 * result + bounds.hashCode();
            return 31 * result + Boolean.hashCode(orthophotoActive);
        }
    }
}
