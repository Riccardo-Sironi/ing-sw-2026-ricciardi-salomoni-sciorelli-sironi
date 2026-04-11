package it.polimi.gc06.mesos.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerMain {

    private final static List<Match> matches = new ArrayList<>();

    public static void main(String[] args){
        int portNumber = 1234;
        if(args.length == 1) portNumber = Integer.parseInt(args[0]);

        System.out.println("Server started.");

        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(portNumber);
        }catch(IOException e){
            e.printStackTrace();
            return;
        }

        System.out.println("Listening on port "+ portNumber +".");
        while(true){
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            new Thread(new ClientDispatcher(clientSocket)).start();
        }

    }

    public synchronized static void addToMatches(int matchId, int numOfPLayers) throws IllegalArgumentException{
        if(matchId < 0) throw new IllegalArgumentException("MatchId should be positive.");
        if(numOfPLayers > Match.MAX_PLAYERS || numOfPLayers < Match.MIN_PLAYERS){
            throw new IllegalArgumentException("NumOfPlayers should be between "+Match.MIN_PLAYERS+" and "+Match.MAX_PLAYERS+".");
        }
        if(doesMatchExist(matchId)) throw new IllegalArgumentException("Match '"+matchId+"' already exist.");
        matches.add(new Match(matchId,numOfPLayers));
    }

    public synchronized static List<Match> getValidMatches(){
        return matches.stream().filter(m -> !m.hasStarted() && !m.isFull()).toList();
    }

    public synchronized static boolean doesMatchExist(int matchId){
        return matches.stream().anyMatch(x -> x.getMatchId() == matchId);
    }

    public synchronized static boolean isNicknameValid(String nick){
        return matches.stream().flatMap(m -> m.getPlayers().stream()).noneMatch(s -> s.equals(nick));
    }

    public synchronized static void addToMatch(String nickname, Socket clientSocket, int matchId) throws IllegalArgumentException, IllegalStateException{
        if(clientSocket == null) throw new IllegalArgumentException("Client socket cannot be null.");
        if(nickname == null) throw new IllegalArgumentException("Nickname cannot be null.");
        if(!isNicknameValid(nickname)) throw new IllegalArgumentException("Nickname already exists.");
        if(!doesMatchExist(matchId)) throw new IllegalArgumentException("MatchId does not exist.");


        Match match = matches.stream().filter(m -> m.getMatchId() == matchId).toList().getFirst();
        try {
            match.addPlayer(new ClientManager(clientSocket, nickname));
        } catch(IOException e){
            System.out.print("Error while starting match: ");
            e.printStackTrace();
        }
    }

}
