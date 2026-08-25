package ee.matteus.plaanisepp.core.model;

import java.util.UUID;

public class ChecklistItem {
    private final String id;
    private String text;
    private boolean completed;

    public ChecklistItem(String text) {
        this(UUID.randomUUID().toString(), text, false);
    }

    public ChecklistItem(String id, String text, boolean completed) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Checklist'i kirje tunnus ei tohi olla tühi.");
        }
        this.id = id;
        rename(text);
        this.completed = completed;
    }

    public String id() {
        return id;
    }

    public String text() {
        return text;
    }

    public void rename(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Checklist'i kirje tekst ei tohi olla tühi.");
        }
        this.text = text.trim();
    }

    public boolean completed() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
