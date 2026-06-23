package it.polimi.gc06.mesos.network.server.matches;

import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.network.server.VirtualClient;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Manages the list of active game matches and tracks globally logged-in users.
 * This class acts as a manager, handling user sessions across the server
 * and ensuring matches are safely created, joined, and automatically cleaned up once finished.
 */
public class MatchManager {

    // All these members are Thread Safe, since we're working across multiple threads
    private final Map<Integer, Match> activeMatches;
    private final Set<String> loggedUsers;
    private final AtomicInteger idGenerator;

    /**
     * Constructs a MatchManager with a specific starting ID for new matches.
     *
     * @param largestResumedMatchId The largest ID among the restored matches, to ensure uniqueness.
     */
    public MatchManager(int largestResumedMatchId) {
        activeMatches = new ConcurrentHashMap<>();
        loggedUsers = ConcurrentHashMap.newKeySet();
        idGenerator = new AtomicInteger(largestResumedMatchId + 1);
    }

    /**
     * Constructs a MatchManager starting with match ID 0.
     */
    public MatchManager() {
        activeMatches = new ConcurrentHashMap<>();
        loggedUsers = ConcurrentHashMap.newKeySet();
        idGenerator = new AtomicInteger();
    }

    /**
     * Attempts to register a user globally on the server.
     *
     * @param nick The nickname to log in.
     * @return True if the nickname was successfully registered, false if it is already taken.
     */
    public boolean login(String nick) {
        if (loggedUsers.add(nick)) {
            System.out.println("Logged in user: " + nick);
            return true;
        }
        return false;
    }

    /**
     * Logs out a user, gracefully removing them from any active matches
     * and freeing up their nickname for future connections.
     *
     * @param nick The nickname of the user to log out.
     * @return True if the user was successfully logged out.
     */
    public synchronized boolean logout(String nick) {
        activeMatches.forEach((_, m) -> m.removePlayer(nick));
        return loggedUsers.remove(nick);
    }

    /**
     * Creates a new {@link Match} and automatically handles the cleanup
     * of matches that have already ended.
     *
     * @param numOfPlayers The required number of players for the new match.
     * @return The newly created match instance.
     */
    public synchronized Match createMatch(int numOfPlayers) {
        removeClosedMatches();
        //creates match
        Match newMatch = new Match(idGenerator.getAndIncrement(), numOfPlayers);
        activeMatches.put(newMatch.getMatchId(), newMatch);
        return newMatch;
    }

    /**
     * Removes from the list all closed matches.
     */
    private void removeClosedMatches() {
        //check if a match should be removed (memory leak handling)
        List<Match> removables = activeMatches.values().stream().filter(Match::hasEnded).toList();
        activeMatches.values().removeAll(removables);
    }

    /**
     * Retrieves a formatted, comma-separated string of matches that can still be joined.
     *
     * @return A formatted string of available matches (e.g. "id:currentPlayers/maxPlayers,...").
     */
    public String getAvailableMatchesString() {
        removeClosedMatches();
        return activeMatches.values().stream()
                .filter(m -> !m.isFull() && !m.hasStarted())
                .map(m -> "Match " + m.getMatchId() + ": "
                        + m.getMatchNumOfPlayers() + "/"
                        + m.getMatchMaxPlayers() + " players")
                .collect(Collectors.joining(","));
    }

    /**
     * Retrieves a formatted, comma-separated string of matches that can still be joined by the specified player.
     *
     * @param nickname The nickname of the player.
     * @return A formatted string of available matches (e.g. "id:currentPlayers/maxPlayers,...").
     */
    public String getAvailableMatchesString(String nickname) {
        removeClosedMatches();
        //saves only visible matches
        List<Match> playerActiveMatches = new ArrayList<>();
        PreviousPlayerMatchVisitor visitor = new PreviousPlayerMatchVisitor(nickname);
        for (Match m : activeMatches.values()) {
            m.accept(visitor);
            if (visitor.canEnter()) playerActiveMatches.add(m);
        }
        //excludes all started or ended matches
        return playerActiveMatches.stream()
                .filter(m -> !m.isFull() && !m.hasStarted())
                .map(m -> "Match " + m.getMatchId() + ": "
                        + m.getMatchNumOfPlayers() + "/"
                        + m.getMatchMaxPlayers() + " players")
                .collect(Collectors.joining(","));
    }

    /**
     * Returns all not ended matches.
     *
     * @return A {@link Collection} view of active matches.
     */
    public synchronized Collection<Match> getActiveMatches() {
        removeClosedMatches();
        return activeMatches.values();
    }

    /**
     * Retrieves the match ID of the match a player is currently in, if any.
     *
     * @param nickname The player's nickname.
     * @return The match ID, if the player is in any. Otherwise, returns -1.
     */
    public int getPlayersMatchId(String nickname) {
        removeClosedMatches();
        for (Match match : activeMatches.values()) {
            if (match.hasPlayer(nickname)) {
                return match.getMatchId();
            }
        }
        return -1; // Not found
    }

    /**
     * Retrieves the ratio of current players to max players for a specific match.
     *
     * @param matchId The ID of the match to query.
     * @return A formatted string "current/max".
     */
    public String getMatchInfo(int matchId) {
        removeClosedMatches();
        Match match = activeMatches.values().stream().filter(m -> m.getMatchId() == matchId).findFirst().orElse(null);
        if (match == null) return "Match not found";
        return match.getMatchNumOfPlayers() + "/" + match.getMatchMaxPlayers();
    }

    /**
     * Connects a {@link VirtualClient} to a specific match, running required thread-safety
     * checks to ensure the match isn't full or already in progress.
     *
     * @param matchId The ID of the match to join.
     * @param client The client requesting to join.
     * @return True if the client successfully joined the match, false otherwise.
     */
    public boolean joinMatch(int matchId, VirtualClient client) {
        removeClosedMatches();
        Match match = activeMatches.values().stream().filter(m -> m.getMatchId() == matchId).findFirst().orElse(null);
        if (match == null) return false;
        synchronized (match) {
            if (match.isFull() || match.hasStarted()) return false;
            try {
                return match.addPlayer(client);
            } catch (IOException _) {
                activeMatches.values().remove(match); //match is not safe
                match.killMatch();
                System.err.print("Error: match '" + match.getMatchId() + "' is compromised.");
                return false;
            }
        }
    }

    /**
     * Create a match identical to the one lost due to server crash.
     *
     * @param matchId The previous match id.
     * @param model The game model representing the previous game state.
     */
    public void restoreMatch(int matchId, GameModel model) {
        RestoredMatch match = new RestoredMatch(matchId, model);
        activeMatches.put(matchId, match);
        System.out.println("Restored match: " + matchId);
    }

    /**
     * Checks if a user is logged in by his nickname.
     *
     * @param nickname The nickname of the user that will be checked.
     * @return True if the user is logged in, false otherwise.
     */
    public boolean isUserLogged(String nickname) {
        return loggedUsers.contains(nickname);
    }

    /**
     * Checks if a match has started.
     *
     * @param id The id of {@link Match} that will be checked.
     * @return True if the match has started, false otherwise.
     */
    public boolean hasMatchStarted(int id) {
        removeClosedMatches();
        return activeMatches.values().stream().filter(m -> m.getMatchId() == id)
                .allMatch(Match::hasStarted);
    }

    /**
     * Checks if a match has ended.
     *
     * @param id The id of {@link Match} that will be checked.
     * @return True if the match has ended, false otherwise.
     */
    public boolean hasMatchEnded(int id) {
        removeClosedMatches();
        return activeMatches.values().stream().filter(m -> m.getMatchId() == id)
                .allMatch(Match::hasEnded);
    }

    /**
     * Checks if a match is running (started but not ended).
     *
     * @param id The id of {@link Match} that will be checked.
     * @return True if the match is running, false otherwise.
     */
    public boolean isMatchRunning(int id) {
        removeClosedMatches();
        return hasMatchStarted(id) && !hasMatchEnded(id);
    }
}