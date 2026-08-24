package ee.matteus.plaanisepp.gui;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;

final class PlanHistory<T> {
    private final int maximumUndoSteps;
    private final Deque<T> undoStates = new ArrayDeque<>();
    private final Deque<T> redoStates = new ArrayDeque<>();
    private T currentState;

    PlanHistory(int maximumUndoSteps) {
        if (maximumUndoSteps <= 0) {
            throw new IllegalArgumentException("Ajaloo sammude arv peab olema positiivne.");
        }
        this.maximumUndoSteps = maximumUndoSteps;
    }

    void reset(T initialState) {
        currentState = Objects.requireNonNull(initialState);
        undoStates.clear();
        redoStates.clear();
    }

    void record(T newState) {
        T state = Objects.requireNonNull(newState);
        if (currentState == null) {
            reset(state);
            return;
        }
        if (currentState.equals(state)) {
            return;
        }
        undoStates.addLast(currentState);
        while (undoStates.size() > maximumUndoSteps) {
            undoStates.removeFirst();
        }
        currentState = state;
        redoStates.clear();
    }

    void replaceCurrent(T state) {
        currentState = Objects.requireNonNull(state);
    }

    Optional<T> undo() {
        if (undoStates.isEmpty()) {
            return Optional.empty();
        }
        redoStates.addLast(currentState);
        currentState = undoStates.removeLast();
        return Optional.of(currentState);
    }

    Optional<T> redo() {
        if (redoStates.isEmpty()) {
            return Optional.empty();
        }
        undoStates.addLast(currentState);
        currentState = redoStates.removeLast();
        return Optional.of(currentState);
    }

    boolean canUndo() {
        return !undoStates.isEmpty();
    }

    boolean canRedo() {
        return !redoStates.isEmpty();
    }
}
