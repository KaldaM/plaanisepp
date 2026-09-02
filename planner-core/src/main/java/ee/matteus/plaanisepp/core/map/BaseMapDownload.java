package ee.matteus.plaanisepp.core.map;

public record BaseMapDownload(
        BaseMapBounds bounds,
        int width,
        int height,
        byte[] regularMap,
        byte[] orthophoto
) {
    public BaseMapDownload {
        if (bounds == null || width <= 0 || height <= 0
                || regularMap == null || regularMap.length == 0
                || orthophoto == null || orthophoto.length == 0) {
            throw new IllegalArgumentException("Allalaaditud aluskaardi andmed ei ole täielikud.");
        }
        regularMap = regularMap.clone();
        orthophoto = orthophoto.clone();
    }

    @Override
    public byte[] regularMap() {
        return regularMap.clone();
    }

    @Override
    public byte[] orthophoto() {
        return orthophoto.clone();
    }

    public double pixelsPerMetre() {
        return width / bounds.widthMetres();
    }
}
