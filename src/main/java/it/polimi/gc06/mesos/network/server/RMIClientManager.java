package it.polimi.gc06.mesos.network.server;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.controller.commands.ControllerCommand;
import it.polimi.gc06.mesos.dtos.ErrorDTO;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.model.DTONotifier;
import it.polimi.gc06.mesos.network.client.ServerConnection;
import it.polimi.gc06.mesos.network.server.matches.MatchManager;

import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Server-side wrapper representing an active RMI client session.
 * Maps incoming abstractions to standard controller logic flows and pushes asynchronous
 * updates (DTOs) from the server model queue straight to the active RMI client interface.
 */
public class RMIClientManager implements VirtualClient {
    private final String nickname;
    private final ServerConnection rmiClient;
    private final MatchManager sharedManager;
    private final BlockingQueue<SmallModelEditor> noticeQueue;
    private BlockingQueue<ControllerCommand> actionQueue;
    private boolean closed;

    /**
     * Instantiates an active managed queue parsing events into standard model connections.
     *
     * @param nickname      the player executing the request
     * @param rmiClient     the connected endpoint remote reference to the client interface
     * @param sharedManager the core game match manager to handle global session tracking and matchmaking interactions
     */
    public RMIClientManager(String nickname, ServerConnection rmiClient, MatchManager sharedManager) {
        this.nickname = nickname;
        this.rmiClient = rmiClient;
        this.sharedManager = sharedManager;
        this.noticeQueue = new LinkedBlockingQueue<>();
        this.closed = false;
        actionQueue = null;
    }

    /**
     * Retrieves the nickname of the client connected via this RMI session.
     *
     * @return the player nickname string
     */
    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void subscribeToNotifier(DTONotifier notifier) {
        notifier.addListener(this, nickname);
    }

    /**
     * Sets the blocking queue used to pipe parsed network inputs into the game controller logic.
     *
     * @param queue the queue holding incoming command wrappers
     */
    @Override
    public void setActionQueue(BlockingQueue<ControllerCommand> queue) {
        this.actionQueue = queue;
    }

    /**
     * Enqueues an incoming player action wrapper into the designated action queue.
     * If the action queue has not been bound yet, it pushes an error backwards to the client.
     *
     * @param command the user-requested gameplay action
     */
    public void enqueueCommand(ControllerCommand command) {
        if (actionQueue != null) {
            try {
                actionQueue.put(command);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            sendErrorMessage("The match hasn't started yet!");
        }
    }

    /**
     * Manually formats an arbitrary string message into an ErrorDTO to be sent
     * backwards to the interacting client for UI display.
     *
     * @param message the specific error format explaining the issue
     */
    @Override
    public void sendErrorMessage(String message) {
        try {
            // Fake event to notify the Error
            noticeQueue.put(new ErrorDTO(message));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Consumes pending updates pushed into the notice queue by game controllers,
     * transmitting them via RMI directly to the mapped client GUI.
     * Acts as an infinite loop actively pinging connections over time bounds.
     */
    @Override
    public void run() {
        while (!closed) {
            try {
                // Wait for max 5 sec
                SmallModelEditor notice = noticeQueue.poll(60, TimeUnit.SECONDS);
                if (notice != null) {
                    rmiClient.receiveDTO(notice);
                } else {
                    // Ping to make sure the client is still alive
                    // This method will throw an exception if the client is not reachable, which will be caught and handled in the catch block below
                    rmiClient.ping();
                }
            } catch (InterruptedException e) {
                return;
            } catch (RemoteException e) {
                System.err.println("Couldn't contact the client '" + nickname + "' for more than 60 seconds. It is probably dead. Closing connection");
                closeConnection();
                return;
            } catch (Exception e) {
                System.err.println("Lost connection with RMI Client '" + nickname + "'.");
                closeConnection();
                return;
            }
        }
    }

    /**
     * Closes the active endpoint connection systematically and disconnects inner observer targets.
     */
    @Override
    public void closeConnection() {
        if (closed) return;
        closed = true;
        if (nickname != null) sharedManager.logout(nickname);
    }

    /**
     * Pushes an incoming DTO update into the notice queue to be transmitted to the client interface
     *
     * @param dto the update to be transmitted
     */
    @Override
    public void update(SmallModelEditor dto) {
        try {
            noticeQueue.put(dto);
        } catch (InterruptedException e) {
        }
    }
}
