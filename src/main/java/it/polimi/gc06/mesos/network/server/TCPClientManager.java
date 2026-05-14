package it.polimi.gc06.mesos.network.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.ErrorDTO;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.network.socket.commands.ControllerCommand;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TCPClientManager implements VirtualClient, ModelListener {

    private BlockingQueue<ControllerCommand> actionQueue;
    private final Socket socket;
    private final String nickname;
    private final MatchManager sharedManager;
    private GameController controller;
    private final BlockingQueue<SmallModelEditor> noticeQueue;
    private final ObjectInputStream inFromClient;
    private final ObjectOutputStream outToClient;
    private boolean closed;

    public TCPClientManager(Socket socket, String nickname, MatchManager sharedManager, ObjectInputStream inFromClient,
                            ObjectOutputStream outToClient) {
        this.socket = socket;
        this.nickname = nickname;
        this.sharedManager = sharedManager;
        noticeQueue = new LinkedBlockingQueue<>();
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
        closed = false;
        actionQueue = null;
    }

    /**
     * Controller setter, should be called before run() or start() method.
     *
     * @param controller the game controller.
     */
    public void setController(GameController controller) {
        this.controller = controller;
        controller.addListener(this, nickname);
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public void setActionQueue(BlockingQueue<ControllerCommand> queue) {
        this.actionQueue = queue;
    }

    @Override
    public void run() {
        //prepares the sender that responds to listener notice, necessary to ensure thread-safe notice
        Thread sender = new Thread(this::senderLoop);
        sender.start();
        //client input loop
        ControllerCommand command;

        try {
            while (true) {

                command = (ControllerCommand) inFromClient.readObject();
                //handling client input
                System.out.println("'" + nickname + "' client sent: " + command);
                try {
                    if (actionQueue != null) {
                        actionQueue.put(command);
                    } else {
                        sendErrorMessage("The match hasn't started yet!");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    sendErrorMessage(e.getMessage());
                    System.err.println("['" + nickname + "'] Unexpected error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.print("Error on '" + nickname + "' client thread: ");
            e.printStackTrace();
        } catch (EOFException _){
            System.out.println(nickname+" disconnected");
        } catch (IOException _){
            System.err.println("Error on '" + nickname + "' client read: ");
        }

        closeConnection();
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


    //it should never be called directly! only usable by a different Thread
    private void senderLoop() {

        //notice loop
        while (!closed) {
            ObjectMapper mapper = new ObjectMapper();
            SmallModelEditor notice = null;
            try {
                notice = noticeQueue.take();
            } catch (InterruptedException _) {
                return;
            }
            try {
                outToClient.writeObject(notice);
            } catch (IOException e) {
                System.err.print("Error on '" + nickname + "' notice dispatch: ");
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() {
        if (closed) return;
        closed = true;
        if (outToClient != null) try {
            outToClient.close();
        } catch (IOException _) {
        }
        if (nickname != null) sharedManager.logout(nickname);
        controller.removeListener(this, nickname);
        try {
            if (!socket.isClosed()) socket.close();
        } catch (IOException _) {
        }
        if (inFromClient != null) try {
            inFromClient.close();
        } catch (IOException _) {
        }
    }

    @Override
    public void update(SmallModelEditor dto) {
        try {
            noticeQueue.put(dto);
        } catch (InterruptedException _) {
        }
    }
}
