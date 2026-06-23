package it.polimi.gc06.mesos.controller;

import it.polimi.gc06.mesos.dtos.SmallModelEditor;

/**
 * An observer interface that listens for updates from the game model.
 * Implementations of this interface can receive and apply DTOs to keep local states synchronized.
 */
public interface ModelListener {

    /**
     * Invoked when the model dispatches a state update.
     *
     * @param dto The SmallModelEditor containing the update data.
     */
    void update(SmallModelEditor dto);
}