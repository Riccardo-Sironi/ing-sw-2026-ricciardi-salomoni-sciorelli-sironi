package it.polimi.gc06.mesos.network.socket;

import it.polimi.gc06.mesos.network.server.Match;
import it.polimi.gc06.mesos.network.server.MatchManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

// ClientDispatcher is needed for deciding to what Match the client should participate
public class ClientDispatcher implements Runnable {

    private final Socket clientSocket;
    private final MatchManager sharedManager;
    private final static int TIMEOUT = 20 * 60 * 1000; //20 minutes

    public ClientDispatcher(Socket clientSocket, MatchManager sharedManager) {
        this.clientSocket = clientSocket;
        this.sharedManager = sharedManager;
    }

    @Override
    public void run() {

        PrintWriter outToClient = null;
        BufferedReader inFromClient = null;
        String nickname = null;

        try {
            clientSocket.setSoTimeout(TIMEOUT);

            inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToClient = new PrintWriter(clientSocket.getOutputStream(), true);
            String input;
            String output;

            //nickname handling
            nickname = inFromClient.readLine(); //client side: nickname request
            while (!sharedManager.login(nickname)) {
                outToClient.println("KO");
                nickname = inFromClient.readLine();
            }
            outToClient.println("OK");

            //match handling
            boolean success = false; //whether ot not the match request was dispatched
            while (!success) {
                input = inFromClient.readLine(); //client side: create match or join match decision
                if (input.equals("CREATE")) {
                    outToClient.println("OK");
                    input = inFromClient.readLine(); //client side: num of player request
                    if (input == null) throw new NullPointerException(); //disconnection handling
                    while (!isNumeric(input) || Integer.parseInt(input) < Match.MIN_PLAYERS
                            || Integer.parseInt(input) > Match.MAX_PLAYERS) {
                        outToClient.println("KO");
                        input = inFromClient.readLine();
                    }
                    clientSocket.setSoTimeout(0); //removes the timeout to ensure match confirm
                    Match newMatch = sharedManager.createMatch(Integer.parseInt(input));
                    sharedManager.joinMatch(newMatch.getMatchId(), clientSocket, nickname);
                    outToClient.println("OK");
                    success = true;
                } else if (input.equals("JOIN")) {
                    outToClient.println(sharedManager.getAvailableMatchesString());
                    input = inFromClient.readLine(); //client side: matches request
                    if (input == null) throw new NullPointerException(); //disconnection handling
                    while (!isNumeric(input) || !sharedManager.joinMatch(Integer.parseInt(input), clientSocket, nickname)) {
                        outToClient.println(sharedManager.getAvailableMatchesString());
                        input = inFromClient.readLine();
                    }
                    clientSocket.setSoTimeout(0); //removes the timeout to ensure match confirm
                    outToClient.println("OK");
                    success = true;
                } else outToClient.println("KO");
            }
        } catch (SocketTimeoutException _) {
            if (outToClient != null) {
                outToClient.println("TIMEOUT");
                outToClient.close();
                String alias = nickname;
                if (alias == null) alias = "not identified";
                System.err.println("Client '" + alias + "' timeout.");
            }
            if (inFromClient != null) try {
                inFromClient.close();
            } catch (IOException _) {
            }
            if (nickname != null) sharedManager.logout(nickname);
        } catch (IOException | NullPointerException | IllegalArgumentException e) {
            //NullPointerException could be thrown if the client disconnects and readLine() returns null
            System.err.print("Error while accepting player: ");
            e.printStackTrace();
            if (outToClient != null) outToClient.close();
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
