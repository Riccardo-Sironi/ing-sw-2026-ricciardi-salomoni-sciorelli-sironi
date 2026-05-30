package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DTONotifier {

    private final ArrayList<ModelListener> listeners;
    private final Map<String, ModelListener> listenerMap;
    private final AtomicInteger sequencer;

    public DTONotifier(){
        this.listeners = new ArrayList<>();
        this.listenerMap = new HashMap<>();
        this.sequencer = new AtomicInteger();
    }

    public void addListener(ModelListener listener, String nickname) {
        listeners.add(listener);
        listenerMap.put(nickname, listener);
    }

    public void removeListener(ModelListener listener, String nickname) {
        listeners.remove(listener);
        listenerMap.remove(nickname);
    }

    /**
     * Sends an update to all model listeners. Should be used only by the model and when the game is already started.
     * Sets the correct sequence number.
     *
     * @param dto the {@link SmallModelEditor} responsible for the notification.
     * @throws IllegalArgumentException if the dto is null.
     */
    public void notifyChange(SmallModelEditor dto) throws IllegalArgumentException{
        if(dto == null) throw new IllegalArgumentException();
        dto.setSequenceNumber(sequencer.getAndIncrement());
        listeners.forEach(l -> l.update(dto));
    }

    /**
     * Sends an update to specific player. Should be used only by the model and when the game is already started.
     * Does not specify a sequence number since it's not a global DTO.
     *
     * @param dto the {@link SmallModelEditor} responsible for the notification.
     * @throws IllegalArgumentException if the dto is null.
     */
    public void notifyChangeToPlayer(String nickname, SmallModelEditor dto) throws IllegalArgumentException{
        if(dto == null) throw new IllegalArgumentException();
        listenerMap.get(nickname).update(dto);
    }
}
