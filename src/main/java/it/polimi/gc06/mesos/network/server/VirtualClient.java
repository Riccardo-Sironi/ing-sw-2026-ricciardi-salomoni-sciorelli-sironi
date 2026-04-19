package it.polimi.gc06.mesos.network.server;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.network.socket.commands.Command;

import java.util.concurrent.BlockingQueue;

public interface VirtualClient extends Runnable {
    String getNickname();

    void setController(GameController controller);

    void closeConnection();

    void setActionQueue(BlockingQueue<Command> queue);

    void sendErrorMessage(String message);

}

