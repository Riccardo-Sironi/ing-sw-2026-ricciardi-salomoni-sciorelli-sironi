package it.polimi.gc06.mesos.network.server;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.controller.commands.ControllerCommand;
import it.polimi.gc06.mesos.model.DTONotifier;

import java.util.concurrent.BlockingQueue;

public interface VirtualClient extends Runnable, ModelListener {
    String getNickname();

    void closeConnection();

    void setActionQueue(BlockingQueue<ControllerCommand> queue);

    void sendErrorMessage(String message);

    void subscribeToNotifier(DTONotifier notifier);
}

