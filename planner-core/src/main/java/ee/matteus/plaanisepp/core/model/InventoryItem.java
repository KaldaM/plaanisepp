package ee.matteus.plaanisepp.core.model;

public final class InventoryItem {
    private String name;
    private int quantity;
    private String notes;

    public InventoryItem(String name, int quantity, String notes) {
        rename(name);
        setQuantity(quantity);
        setNotes(notes);
    }

    public String name() {
        return name;
    }

    public void rename(String name) {
        this.name = name == null ? "" : name.trim();
    }

    public int quantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Inventari kogus ei saa olla negatiivne.");
        }
        this.quantity = quantity;
    }

    public String notes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes.trim();
    }
}
