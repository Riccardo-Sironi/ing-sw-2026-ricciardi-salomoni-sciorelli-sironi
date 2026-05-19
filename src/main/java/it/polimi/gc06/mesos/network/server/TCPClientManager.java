package it.polimi.gc06.mesos.network.server;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.controller.commands.ControllerCommand;
import it.polimi.gc06.mesos.dtos.ErrorDTO;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;

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

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void setController(GameController controller) {
        controller.addListener(this, nickname);
        this.controller = controller;
    }

    @Override
    public void closeConnection() {
        controller.removeListener(this, nickname);
        Thread.currentThread().interrupt();
    }

    @Override
    public void setActionQueue(BlockingQueue<ControllerCommand> queue) {
        this.actionQueue = queue;
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

    @Override
    public void update(SmallModelEditor dto) {
        try {
            noticeQueue.put(dto);
        } catch (InterruptedException e) {}
    }
}
