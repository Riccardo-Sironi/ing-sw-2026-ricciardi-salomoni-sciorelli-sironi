package it.polimi.gc06.mesos.network.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

// ClientDispatcher is needed for deciding to what Match the client should participate
public class TCPClientDispatcher implements Runnable {

    private final Socket clientSocket;
    private final MatchManager sharedManager;
    private final static int TIMEOUT = 20 * 60 * 1000; //20 minutes

    public TCPClientDispatcher(Socket clientSocket, MatchManager sharedManager) {
        this.clientSocket = clientSocket;
        this.sharedManager = sharedManager;
    }

    @Override
    public void run() {

        ObjectOutputStream outToClient = null;
        ObjectInputStream inFromClient = null;
        String nickname = null;

        try {
            clientSocket.setSoTimeout(TIMEOUT);

            outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
            inFromClient = new ObjectInputStream(clientSocket.getInputStream());
            String input;

            //nickname handling
            nickname = (String)inFromClient.readObject(); //client side: nickname request
            while (!sharedManager.login(nickname)) {
                outToClient.writeObject("KO");
                nickname = (String)inFromClient.readObject();
            }
            outToClient.writeObject("OK");

            //match handling
            boolean success = false; //whether ot not the match request was dispatched
            while (!success) {
                input = (String)inFromClient.readObject(); //client side: create match or join match decision
                if (input.equals("CREATE")) {
                    outToClient.writeObject("OK");
                    input = (String)inFromClient.readObject(); //client side: num of player request
                    if (input == null) throw new NullPointerException(); //disconnection handling
                    while (!isNumeric(input) || Integer.parseInt(input) < Match.MIN_PLAYERS
                            || Integer.parseInt(input) > Match.MAX_PLAYERS) {
                        outToClient.writeObject("KO");
                        input = (String)inFromClient.readObject();
                    }
                    clientSocket.setSoTimeout(0); //removes the timeout to ensure match confirm
                    Match newMatch = sharedManager.createMatch(Integer.parseInt(input));
                    sharedManager.joinMatch(newMatch.getMatchId(), new TCPClientManager(clientSocket, nickname,
                            sharedManager, inFromClient, outToClient));
                    outToClient.writeObject("OK");
                    success = true;
                } else if (input.equals("JOIN")) {
                    outToClient.writeObject(sharedManager.getAvailableMatchesString());
                    input = (String)inFromClient.readObject(); //client side: matches request
                    if (input == null) throw new NullPointerException(); //disconnection handling
                    while (!isNumeric(input) || !sharedManager.joinMatch(Integer.parseInt(input), new TCPClientManager(
                            clientSocket, nickname, sharedManager, inFromClient, outToClient))) {
                        outToClient.writeObject(sharedManager.getAvailableMatchesString());
                        input = (String)inFromClient.readObject();
                    }
                    clientSocket.setSoTimeout(0); //removes the timeout to ensure match confirm
                    outToClient.writeObject("OK");
                    success = true;
                } else outToClient.writeObject("KO");
            }
        } catch (SocketTimeoutException _) {
            if (outToClient != null) {
                try {
                    outToClient.writeObject("TIMEOUT");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String alias = nickname;
                if (alias == null) alias = "not identified";
                System.err.println("Client '" + alias + "' timeout.");
            }
            if (inFromClient != null) try {
                inFromClient.close();
            } catch (IOException _) {
            }
            if (nickname != null) sharedManager.logout(nickname);
        } catch (IOException | NullPointerException | IllegalArgumentException | ClassNotFoundException e) {
            //NullPointerException could be thrown if the client disconnects and readLine() returns null
            System.err.print("Error while accepting player: ");
            e.printStackTrace();
            if (inFromClient != null) try {
                inFromClient.close();
            } catch (IOException _) {
            }
            if (nickname != null) sharedManager.logout(nickname);
        }
    }

    private boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException _) {
            return false;
        }
    }
}
