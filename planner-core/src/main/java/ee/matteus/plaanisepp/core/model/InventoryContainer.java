package ee.matteus.plaanisepp.core.model;

import java.util.List;

public interface InventoryContainer {
    List<InventoryItem> inventoryItems();

    void addInventoryItem(InventoryItem item);

    void removeInventoryItem(int index);
}
