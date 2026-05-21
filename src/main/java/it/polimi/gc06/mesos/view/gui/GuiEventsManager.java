package it.polimi.gc06.mesos.view.gui;

import javafx.application.Platform;

import java.util.concurrent.ConcurrentLinkedQueue;

public class GuiEventsManager {
    private final ConcurrentLinkedQueue<Runnable> eventQueue = new ConcurrentLinkedQueue<>();
    private boolean isAnimating = false;

    public void enqueueEvent(Runnable uiAction) {
        eventQueue.add(uiAction);
        processNextEvent();
    }

    private void processNextEvent() {
        Platform.runLater(() -> {
            if (isAnimating || eventQueue.isEmpty()) {
                return;
            }

            isAnimating = true;

            Runnable nextAction = eventQueue.poll();

            if (nextAction != null) {
                nextAction.run();
            }
        });
    }

    public void onAnimationFinished() {
        Platform.runLater(() -> {
            isAnimating = false;
            processNextEvent();
        });
    }
}
