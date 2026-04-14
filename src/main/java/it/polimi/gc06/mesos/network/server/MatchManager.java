package it.polimi.gc06.mesos.network.server;

import it.polimi.gc06.mesos.network.socket.ClientManager;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MatchManager {
    private final Map<Integer, Match> activeMatches = new ConcurrentHashMap<>();
    private final Set<String> loggedUsers = ConcurrentHashMap.newKeySet();
    private final static AtomicInteger idGenerator = new AtomicInteger();


    public synchronized boolean login(String nick) {
        return loggedUsers.add(nick);
    }

    public synchronized boolean logout(String nick) {
        activeMatches.forEach((_, m) -> m.removePlayer(nick));
        return loggedUsers.remove(nick);
    }

    public synchronized Match createMatch(int numOfPlayers) {
        //check if a match should be removed (memory leak handling)
        List<Match> removables = activeMatches.values().stream().filter(Match::hasEnded).toList();
        activeMatches.values().removeAll(removables);
        //creates match
        Match newMatch = new Match(idGenerator.getAndIncrement(), numOfPlayers);
        activeMatches.put(idGenerator.getAndIncrement(), newMatch);
        return newMatch;
    }

    public synchronized String getAvailableMatchesString() {
        return activeMatches.values().stream()
                .filter(m -> !m.isFull() || !m.hasStarted())
                .map(m -> m.getMatchId() + ":"
                        + m.getMatchNumOfPlayers() + "/"
                        + m.getMatchMaxPlayers())
                .collect(Collectors.joining(","));
    }

    public synchronized boolean joinMatch(int matchId, Socket clientSocket, String nickname) {
        Match match = activeMatches.values().stream().filter(m -> m.getMatchId() == matchId).findFirst().orElse(null);
        if (match == null) return false;
        synchronized (match) {
            if (match.isFull() || match.hasStarted()) return false;
            try {
                match.addPlayer(new ClientManager(clientSocket, nickname, this));
                return true;
            } catch (IOException _) {
                activeMatches.values().remove(match); //match is not safe
                match.killMatch();
                System.err.print("Error: match '" + match.getMatchId() + "' is compromised.");
                return false;
            }
        }
    }
}
