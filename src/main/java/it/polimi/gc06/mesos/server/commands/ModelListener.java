package it.polimi.gc06.mesos.server.commands;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.BlockingQueue;

public class ModelListener implements PropertyChangeListener {

    private final BlockingQueue<PropertyChangeEvent> noticeQueue;

    public ModelListener(BlockingQueue<PropertyChangeEvent> noticeQueue) {
        this.noticeQueue = noticeQueue;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        noticeQueue.add(evt);
    }
}
