package it.polimi.gc06.mesos.network.socket.commands;

import it.polimi.gc06.mesos.controller.GameController;

public interface ControllerCaller {
    public void call(GameController controller, String nickname, int index);
}
