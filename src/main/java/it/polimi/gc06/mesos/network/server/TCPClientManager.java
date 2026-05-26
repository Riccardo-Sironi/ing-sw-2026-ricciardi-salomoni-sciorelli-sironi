package it.polimi.gc06.mesos.network.server;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.controller.commands.ControllerCommand;
import it.polimi.gc06.mesos.dtos.ErrorDTO;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.model.DTONotifier;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TCPClientManager implements VirtualClient{

    private BlockingQueue<ControllerCommand> actionQueue;
    private final BlockingQueue<SmallModelEditor> noticeQueue;
    private final String nickname;
    private final ObjectOutputStream out;
    private GameController controller;

    public TCPClientManager(String nickname, ObjectOutputStream out){
        this.nickname = nickname;
        this.controller = null;
        this.actionQueue = null;
        this.noticeQueue = new LinkedBlockingQueue<>();
        this.out = out;
    }

    /**
     * Enqueues a command for the controller, is a blocking call
     *
     * @param command the command that will be enqueued.
     * @throws InterruptedException if this thread gets interrupted.
     * @throws IllegalArgumentException if command is null.
     */
    public void enqueueCommand(ControllerCommand command) throws InterruptedException, IllegalArgumentException, IllegalStateException{
        if(command == null) throw new IllegalArgumentException();
        if(actionQueue == null) throw new IllegalStateException("Action queue has not been set.");
        actionQueue.put(command);
    }

    /**
     * Gives you the nickname of the player.
     * @return The player's current nickname.
     */
    @Override
    public String getNickname() {
        return nickname;
    }

    /**
     * Hooks this client up to the game controller, allowing it to listen about game updates.
     * @param controller The active game controller.
     */
    @Override
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Cuts the connection logic loose and unregisters event listeners.
     * Basically hangs up the phone on the game.
     */
    @Override
    public void closeConnection() {
        Thread.currentThread().interrupt();
    }

    /**
     * Sets where the controller commands from this client should queue up.
     * @param notifier The queue ready to take incoming player actions.
     */
    @Override
    public void subscribeToNotifier(DTONotifier notifier) {
        notifier.addListener(this, nickname);
    }

    @Override
    public void setActionQueue(BlockingQueue<ControllerCommand> queue) {
        this.actionQueue = queue;
    }

    /**
     * Shoots an error message text as a DTO notice directly to the client screen.
     * @param message The alert text to show.
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
     * Keeps the wheels turning, constantly taking new updates and tossing them
     * over the live TCP output stream down to the client. Stops if things break.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Wait for max 5 sec
                SmallModelEditor notice = noticeQueue.poll(5, TimeUnit.SECONDS);
                if(notice != null) synchronized (out) {
                    out.writeObject(notice);
                    out.flush();
                }
            } catch (InterruptedException | IOException e) {
                System.err.println("Lost connection with TCP Client '" + nickname + "'.");
                closeConnection();
                return;
            }
        }
    }

    /**
     * Drops a newly created small model patch onto the outgoing notice queue.
     * @param dto the update object.
     */
    @Override
    public void update(SmallModelEditor dto) {
        try {
            noticeQueue.put(dto);
        } catch (InterruptedException e) {}
    }
}
