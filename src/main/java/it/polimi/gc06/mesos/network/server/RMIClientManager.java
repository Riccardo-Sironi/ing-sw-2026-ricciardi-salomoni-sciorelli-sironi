package it.polimi.gc06.mesos.network.server;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.controller.commands.ControllerCommand;
import it.polimi.gc06.mesos.dtos.ErrorDTO;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.network.client.ServerConnection;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class RMIClientManager implements VirtualClient {
    private final String nickname;
    private final ServerConnection rmiClient;
    private final MatchManager sharedManager;
    private GameController controller;
    private final BlockingQueue<SmallModelEditor> noticeQueue;
    private BlockingQueue<ControllerCommand> actionQueue;
    private boolean closed;

    public RMIClientManager(String nickname, ServerConnection rmiClient, MatchManager sharedManager) {
        this.nickname = nickname;
        this.rmiClient = rmiClient;
        this.sharedManager = sharedManager;
        this.noticeQueue = new LinkedBlockingQueue<>();
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
        controller.addListener(this, nickname);
    }

    @Override
    public void setActionQueue(BlockingQueue<ControllerCommand> queue) {
        this.actionQueue = queue;
    }

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

    @Override
    public void sendErrorMessage(String message) {
        try {
            // Fake event to notify the Error
            noticeQueue.put(new ErrorDTO(message));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        while (!closed) {
            try {
                // Wait for max 5 sec
                SmallModelEditor notice = noticeQueue.poll(5, TimeUnit.SECONDS);
                if (notice != null) {
                    rmiClient.receiveDTO(notice);
                } else {
                    // Ping to make sure the client is still alive
                    // This method will throw an exception if the client is not reachable, which will be caught and handled in the catch block below
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
        if (controller != null) controller.removeListener(this, nickname);
    }

    public GameController getController() {
        return controller;
    }

    @Override
    public void update(SmallModelEditor dto) {
        try {
            noticeQueue.put(dto);
        } catch (InterruptedException e) {
        }
    }
}
