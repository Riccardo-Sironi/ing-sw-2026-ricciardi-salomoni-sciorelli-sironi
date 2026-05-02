package it.polimi.gc06.mesos.network.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.network.server.MatchManager;
import it.polimi.gc06.mesos.network.server.VirtualClient;
import it.polimi.gc06.mesos.network.socket.commands.Command;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TCPClientManager implements VirtualClient, PropertyChangeListener {

    private BlockingQueue<Command> actionQueue;
    private final Socket socket;
    private final String nickname;
    private final MatchManager sharedManager;
    private GameController controller;
    private final BlockingQueue<PropertyChangeEvent> noticeQueue;
    private BufferedReader inFromClient;
    private PrintWriter outToClient;
    private boolean closed;

    public TCPClientManager(Socket socket, String nickname, MatchManager sharedManager) {
        this.socket = socket;
        this.nickname = nickname;
        this.sharedManager = sharedManager;
        noticeQueue = new LinkedBlockingQueue<>();
        inFromClient = null;
        outToClient = null;
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
        controller.addListener(this);
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public void setActionQueue(BlockingQueue<Command> queue) {
        this.actionQueue = queue;
    }

    @Override
    public void run() {
        //prepares the sender that responds to listener notice, necessary to ensure thread-safe notice
        Thread sender = new Thread(this::senderLoop);
        sender.start();

        ObjectMapper mapper = new ObjectMapper();

        try {
            //prepares input object
            inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //client input loop
            String input = "";
            while ((input = inFromClient.readLine()) != null) {

                //handling client input
                System.out.println("'" + nickname + "' client sent: " + input);
                try {

                    Command command = mapper.readValue(input, Command.class);

                    if (actionQueue != null) {
                        actionQueue.put(command);
                    } else {
                        sendErrorMessage("The match hasn't started yet!");
                    }
                } catch (JsonProcessingException e) {
                    System.err.print("Error on '" + nickname + "' client request: ");

                    //TODO Questo potrebbe essere un modo per propagare l'errore al sender
                    sendErrorMessage(e.getMessage());
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {

                    //TODO Questo potrebbe essere un modo per propagare l'errore al sender
                    sendErrorMessage(e.getMessage());
                    System.err.println("['" + nickname + "'] Unexpected error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.print("Error on '" + nickname + "' client thread: ");
            e.printStackTrace();
        }

        closeConnection();
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


    //it should never be called directly! only usable by a different Thread
    private void senderLoop() {

        //prepares the output object
        try {
            outToClient = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.print("Error on '" + nickname + "' client notice thread: ");
            e.printStackTrace();
            if (nickname != null) sharedManager.logout(nickname);
            return;
        }

        //notice loop
        while (!closed) {
            ObjectMapper mapper = new ObjectMapper();
            PropertyChangeEvent notice = null;
            try {
                notice = noticeQueue.take();
            } catch (InterruptedException _) {
                return;
            }
            try {
                outToClient.println(mapper.writeValueAsString(notice));
            } catch (IOException e) {
                System.err.print("Error on '" + nickname + "' notice dispatch: ");
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() {
        if (closed) return;
        closed = true;
        if (outToClient != null) outToClient.close();
        if (nickname != null) sharedManager.logout(nickname);
        controller.removeListener(this);
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
    public void propertyChange(PropertyChangeEvent evt) {
        noticeQueue.add(evt);
    }
}
