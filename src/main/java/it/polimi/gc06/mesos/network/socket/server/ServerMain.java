package it.polimi.gc06.mesos.network.socket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ServerMain {

    private final static List<Match> matches = new CopyOnWriteArrayList<>();
    private final static Set<String> loggedUser = ConcurrentHashMap.newKeySet();
    private final static AtomicInteger idGenerator = new AtomicInteger();

    public static void main(String[] args) {
        int portNumber = 1234;
        if (args.length == 1) portNumber = Integer.parseInt(args[0]);

        System.out.println("Server started.");

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Listening on port " + portNumber + ".");
        while (true) {
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

    public static boolean login(String nick) {
        return loggedUser.add(nick);
    }

    public static boolean logout(String nick) {
        matches.forEach(m -> m.removePlayer(nick));
        return loggedUser.remove(nick);
    }

    public static void createMatch(int numOfPlayers, Socket clientSocket, String nickname) {
        //check if a match should be removed (memory leak handling)
        List<Match> removables = matches.stream().filter(Match::hasEnded).toList();
        matches.removeAll(removables);
        //creates match
        Match match = new Match(idGenerator.getAndIncrement(), numOfPlayers);
        try {
            match.addPlayer(new ClientManager(clientSocket, nickname));
        } catch (IOException _) {
        } //never occurs: an exception is thrown only if there is more than one player
        matches.add(match);
    }

    public static String getAvailableMatchesString() {
        return matches.stream()
                .filter(m -> !m.isFull() || !m.hasStarted())
                .map(m -> String.valueOf(m.getMatchId()) + ":"
                        + String.valueOf(m.getMatchNumOfPlayers()) + "/"
                        + String.valueOf(m.getMatchMaxPlayers()))
                .collect(Collectors.joining(","));
    }

    public static boolean joinMatch(int matchId, Socket clientSocket, String nickname) {
        Match match = matches.stream().filter(m -> m.getMatchId() == matchId).findFirst().orElse(null);
        if (match == null) return false;
        synchronized (match) {
            if (match.isFull() || match.hasStarted()) return false;
            try {
                match.addPlayer(new ClientManager(clientSocket, nickname));
                return true;
            } catch (IOException _) {
                matches.remove(match); //match is not safe
                match.killMatch();
                System.err.print("Error: match '" + match.getMatchId() + "' is compromised.");
                return false;
            }
        }
    }

}
