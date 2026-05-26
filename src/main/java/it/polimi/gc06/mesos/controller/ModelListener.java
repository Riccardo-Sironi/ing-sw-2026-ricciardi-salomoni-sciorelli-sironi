package it.polimi.gc06.mesos.controller;

import it.polimi.gc06.mesos.dtos.SmallModelEditor;

public interface ModelListener {

    /**
     * Invoked when the model dispatches a state update.
     *
     * @param dto the SmallModelEditor containing the update data.
     */
    void update(SmallModelEditor dto);
}