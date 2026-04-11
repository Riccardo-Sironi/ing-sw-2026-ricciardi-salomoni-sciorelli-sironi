package it.polimi.gc06.mesos.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.stream.Collectors;

// ClientDispatcher is needed for deciding to what Match the client should participate
public class ClientDispatcher implements Runnable{

    private final Socket clientSocket;
    private final static int TIMEOUT = 10*60*1000; //10 minutes

    public ClientDispatcher(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        PrintWriter outToClient = null;
        BufferedReader inFromClient = null;

        try {
            clientSocket.setSoTimeout(TIMEOUT);

            inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToClient =  new PrintWriter(clientSocket.getOutputStream(),true);
            String input;
            String output;

            //nickname handling
            String nickname;
            nickname = inFromClient.readLine(); //client side: nickname request
            while (!ServerMain.isNicknameValid(nickname)){
                outToClient.println("KO");
                nickname = inFromClient.readLine();
            }
            outToClient.println("OK");

            //match handling
            boolean success = false; //whether ot not the match request was dispatched
            while(!success) {
                output = ServerMain.getValidMatches().stream().map(m -> String.valueOf(m.getMatchId()))
                        .collect(Collectors.joining(","));
                outToClient.println(output); //client side: prints all available match
                input = inFromClient.readLine(); //client side: matches request, new id -> new match
                while (!isNumeric(input)) {
                    outToClient.println("KO");
                    input = inFromClient.readLine();
                }
                int matchIdRequest = Integer.parseInt(input);
                if (!ServerMain.doesMatchExist(matchIdRequest)){
                    outToClient.println("NUMBER_OF_PLAYERS");
                    input = inFromClient.readLine(); //client side: num of player request
                    while (!isNumeric(input) || Integer.parseInt(input) < Match.MIN_PLAYERS
                            || Integer.parseInt(input) > Match.MAX_PLAYERS) {
                        outToClient.println("KO");
                        input = inFromClient.readLine();
                    }
                    ServerMain.addToMatches(matchIdRequest,Integer.parseInt(input));
                }
                try {
                    ServerMain.addToMatch(nickname, clientSocket, matchIdRequest);
                    clientSocket.setSoTimeout(0); //removes the timeout to ensure login confirm
                    outToClient.println("OK");
                    success = true;
                } catch (IllegalStateException _) {
                    outToClient.println("KO"); //client side: match request fail
                }
            }
        }
        catch (SocketTimeoutException _){
            if(outToClient != null) outToClient.println("TIMEOUT");
        }
        catch (IOException | NullPointerException | IllegalArgumentException e) {
            //NullPointerException could be thrown if the client disconnects and readLine() returns null
            System.err.print("Error while accepting player: ");
            e.printStackTrace();
        }
        finally {
            if(outToClient != null) outToClient.close();
            if(inFromClient != null) try{
                inFromClient.close();
            } catch(IOException _) {}
        }
    }

    private boolean isNumeric(String s){
        try{
            Integer.parseInt(s);
            return true;
        }catch (NumberFormatException _){
            return false;
        }
    }
}
