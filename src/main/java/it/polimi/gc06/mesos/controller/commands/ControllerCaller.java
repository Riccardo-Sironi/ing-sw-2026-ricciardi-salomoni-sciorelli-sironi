package it.polimi.gc06.mesos.controller.commands;

import it.polimi.gc06.mesos.controller.GameController;

public interface ControllerCaller {

    /**
     * This method executes a specific controller method based on the caller implementation.
     *
     * @param controller the game controller on which to invoke the action.
     * @param nickname   the nickname of the player performing the action.
     * @param index      the index parameter relevant to the specific action.
     */
    void call(GameController controller, String nickname, int index);
}
