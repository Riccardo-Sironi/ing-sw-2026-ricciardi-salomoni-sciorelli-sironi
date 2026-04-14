package it.polimi.gc06.mesos.network.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.network.server.MatchManager;

import java.beans.PropertyChangeEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientManager implements Runnable {

    private final Socket socket;
    private final String nickname;
    private final MatchManager sharedManager;
    private GameController controller;
    private final ModelListener listener;
    private final BlockingQueue<PropertyChangeEvent> noticeQueue;
    private BufferedReader inFromClient;
    private PrintWriter outToClient;
    private boolean closed;

    public ClientManager(Socket socket, String nickname, MatchManager sharedManager) {
        this.socket = socket;
        this.nickname = nickname;
        this.sharedManager = sharedManager;
        noticeQueue = new LinkedBlockingQueue<>();
        listener = new ModelListener(noticeQueue);
        inFromClient = null;
        outToClient = null;
        closed = false;
    }

    /**
     * Controller setter, should be called before run() or start() method.
     *
     * @param controller the game controller.
     */
    public void setController(GameController controller) {
        this.controller = controller;
        controller.addListener(listener);
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public void run() {

        //prepares the sender that responds to listener notice, necessary to ensure thread-safe notice
        Thread sender = new Thread(this::senderLoop);
        sender.start();

        try {
            //prepares input object
            inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //client input loop
            String input = "";
            while ((input = inFromClient.readLine()) != null) {

                //handling client input
                System.out.println("'" + nickname + "' client sent: " + input);
                try {
                    controller.parse(input);
                } catch (JsonProcessingException e) {
                    System.err.print("Error on '" + nickname + "' client request: ");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.print("Error on '" + nickname + "' client thread: ");
            e.printStackTrace();
        }

        closeConnection();
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
        controller.removeListener(listener);
        try {
            if (!socket.isClosed()) socket.close();
        } catch (IOException _) {
        }
        if (inFromClient != null) try {
            inFromClient.close();
        } catch (IOException _) {
        }
    }

}
