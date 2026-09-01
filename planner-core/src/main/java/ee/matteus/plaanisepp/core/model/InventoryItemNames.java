package ee.matteus.plaanisepp.core.model;

public final class InventoryItemNames {
    private InventoryItemNames() {
    }

    public static boolean isFence(String name) {
        return matches(name, "aed", "aiad");
    }

    public static boolean isGardenStone(String name) {
        return matches(name, "aiakivi", "aiakivid");
    }

    public static boolean isTent(String name) {
        return matches(name, "telk", "telgid");
    }

    public static boolean isBuiltInInventory(String name) {
        return isFence(name) || isGardenStone(name) || isTent(name);
    }

    private static boolean matches(String name, String singular, String plural) {
        String normalized = name == null ? "" : name.trim();
        return singular.equalsIgnoreCase(normalized) || plural.equalsIgnoreCase(normalized);
    }
}
