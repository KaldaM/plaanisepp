package ee.matteus.plaanisepp.core.map;

public record BaseMapBounds(double minX, double minY, double maxX, double maxY) {
    public BaseMapBounds {
        if (!Double.isFinite(minX) || !Double.isFinite(minY)
                || !Double.isFinite(maxX) || !Double.isFinite(maxY)
                || maxX <= minX || maxY <= minY) {
            throw new IllegalArgumentException("Kaardiala piirid ei ole korrektsed.");
        }
    }

    public double widthMetres() {
        return maxX - minX;
    }

    public double heightMetres() {
        return maxY - minY;
    }
}
