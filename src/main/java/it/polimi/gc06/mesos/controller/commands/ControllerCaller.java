package it.polimi.gc06.mesos.controller.commands;

import it.polimi.gc06.mesos.controller.GameController;

/**
 * Represents an executable action that can be dispatched to the GameController.
 * This interface abstracts the different types of requests a client can make,
 * allowing them to be executed polymorphically.
 */
public interface ControllerCaller {

    /**
     * This method executes a specific controller method based on the caller implementation.
     *
     * @param controller The game controller on which to invoke the action.
     * @param nickname The nickname of the player performing the action.
     * @param index The index parameter relevant to the specific action.
     */
    void call(GameController controller, String nickname, int index);
}