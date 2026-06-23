package it.polimi.gc06.mesos.network.server;

import it.polimi.gc06.mesos.controller.commands.ControllerCommand;
import it.polimi.gc06.mesos.model.DTONotifier;
import java.util.concurrent.BlockingQueue;

/**
 * Defines the interface for a network client connected to the server.
 * Handles the abstraction of network communication, command queueing, and notification subscriptions.
 */
public interface VirtualClient extends Runnable, it.polimi.gc06.mesos.controller.ModelListener {

    /**
     * Retrieves the client nickname.
     *
     * @return The nickname.
     */
    String getNickname();

    /**
     * Closes the client connection.
     */
    void closeConnection();

    /**
     * Sets the action queue for this client.
     *
     * @param queue The BlockingQueue of incoming commands.
     */
    void setActionQueue(BlockingQueue<ControllerCommand> queue);

    /**
     * Sends an error message to the client.
     *
     * @param message The error message content.
     */
    void sendErrorMessage(String message);

    /**
     * Subscribes the client to the model notifier.
     *
     * @param notifier The notifier instance.
     */
    void subscribeToNotifier(DTONotifier notifier);
}