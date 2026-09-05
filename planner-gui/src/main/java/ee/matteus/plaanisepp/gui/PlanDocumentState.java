package ee.matteus.plaanisepp.gui;

import java.io.File;

final class PlanDocumentState {
    private static final String APPLICATION_TITLE = "Plaanisepp";

    private boolean unsavedChanges;
    private java.time.LocalDateTime lastLocalSave;
    private java.time.LocalDateTime lastSync;
    private String saveProblem;
    private boolean saving;

    void beginSave() { saving = true; saveProblem = null; }
    void saveFailed() { saving = false; saveProblem = "Salvestamine ebaõnnestus"; }
    void saveSucceeded() {
        lastLocalSave = java.time.LocalDateTime.now();
        saving = false;
        saveProblem = null;
        markClean();
    }
    void resetSaveInfo() { lastLocalSave = null; lastSync = null; saving = false; saveProblem = null; }
    boolean hasSaveError() { return saveProblem != null; }
    String saveDetails() {
        return "Kohalik salvestus: " + (lastLocalSave == null ? "selles seansis puudub" : lastLocalSave.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))
                + "\nPilvesünkroonimine: " + (lastSync == null ? "pole kasutusel" : lastSync.toString());
    }

    void markDirty() {
        unsavedChanges = true;
    }

    void markClean() {
        unsavedChanges = false;
    }

    boolean hasUnsavedChanges() {
        return unsavedChanges;
    }

    String windowTitle(File currentFile) {
        String unsavedPrefix = unsavedChanges ? "* " : "";
        String fileName = currentFile == null ? "" : " - " + currentFile.getName();
        return unsavedPrefix + APPLICATION_TITLE + fileName;
    }

    String saveStatusText() {
        String status = saving ? "Salvestamine…" : saveProblem != null ? saveProblem
                : unsavedChanges ? "Salvestamata muudatused" : lastLocalSave == null ? "Muudatusi pole" : "Salvestatud";
        return status + (lastLocalSave == null ? "" : " · kohalik " + lastLocalSave.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
}
