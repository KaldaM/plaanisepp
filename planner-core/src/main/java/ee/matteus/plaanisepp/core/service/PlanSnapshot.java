package ee.matteus.plaanisepp.core.service;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public final class PlanSnapshot {
    private final Map<String, String> properties;
    private final MapImageAsset mapImageAsset;

    PlanSnapshot(Map<String, String> properties, MapImageAsset mapImageAsset) {
        this.properties = Map.copyOf(properties);
        this.mapImageAsset = mapImageAsset;
    }

    Map<String, String> properties() {
        return properties;
    }

    MapImageAsset mapImageAsset() {
        return mapImageAsset;
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
                && Objects.equals(mapImageAsset, snapshot.mapImageAsset);
    }

    @Override
    public int hashCode() {
        return 31 * properties.hashCode() + Objects.hashCode(mapImageAsset);
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
}
