package it.polimi.gc06.mesos.network.rmi;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.network.server.MatchManager;
import it.polimi.gc06.mesos.network.server.VirtualClient;
import it.polimi.gc06.mesos.network.socket.ModelListener;
import it.polimi.gc06.mesos.network.socket.commands.Command;

import java.beans.PropertyChangeEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class RMIClientManager implements VirtualClient {
    private final String nickname;
    private final RMIClientInterface rmiClient;
    private final MatchManager sharedManager;
    private GameController controller;
    private final ModelListener listener;
    private final BlockingQueue<PropertyChangeEvent> noticeQueue;
    private BlockingQueue<Command> actionQueue;
    private boolean closed;

    public RMIClientManager(String nickname, RMIClientInterface rmiClient, MatchManager sharedManager) {
        this.nickname = nickname;
        this.rmiClient = rmiClient;
        this.sharedManager = sharedManager;
        this.noticeQueue = new LinkedBlockingQueue<>();
        this.listener = new ModelListener(noticeQueue);
        this.closed = false;
        actionQueue = null;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void setController(GameController controller) {
        this.controller = controller;
        controller.addListener(listener);
    }

    @Override
    public void setActionQueue(BlockingQueue<Command> queue) {
        this.actionQueue = queue;
    }

    public void enqueueCommand(Command command) {
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

    @Override
    public void sendErrorMessage(String message) {
        try {
            // Fake event to notify the Error
            noticeQueue.put(new PropertyChangeEvent(this, "ACTION_ERROR", null, message));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        ObjectMapper mapper = new ObjectMapper();
        while (!closed) {
            try {
                // Wait for max 5 sec
                PropertyChangeEvent notice = noticeQueue.poll(5, TimeUnit.SECONDS);
                if (notice != null) {
                    rmiClient.receiveMessage(mapper.writeValueAsString(notice));
                } else {
                    // Ping to make sure the client is still alive
                    rmiClient.ping();
                }
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                System.err.println("Lost connection with RMI Client '" + nickname + "'.");
                closeConnection();
                return;
            }
        }
    }

    @Override
    public void closeConnection() {
        if (closed) return;
        closed = true;
        if (nickname != null) sharedManager.logout(nickname);
        if (controller != null) controller.removeListener(listener);
    }

    public GameController getController() {
        return controller;
    }
}
