package it.polimi.gc06.mesos.network.server;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.model.DTONotifier;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.InstancesManager.ModelInstancesManager;
import it.polimi.gc06.mesos.network.leaderboard.LeaderboardDAO;
import it.polimi.gc06.mesos.controller.commands.ControllerCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Represents an individual game session hosted on the server.
 * It acts as a bridge between the network layer {@link VirtualClient (Clients)} and the core game logic {@link GameController (GameController)},
 * coordinating the players' action queue and running the main game loop until a winner emerges or the match is aborted.
 */
public class Match {
    private final BlockingQueue<VirtualClient> players;
    private final BlockingQueue<Thread> playersThreads;

    private final BlockingQueue<ControllerCommand> actionQueue;
    private Thread matchExecutorThread;

    private final int matchId;
    private final int matchMaxPlayers;
    private volatile boolean hasStarted;
    private volatile boolean hasBeenForcefullyEnded;
    private GameController controller;

    /**
     * Initializes a new match waiting for players to join.
     *
     * @param matchId      the unique identifier for this match
     * @param numOfPlayers the required capacity of players needed to start the game
     */
    public Match(int matchId, int numOfPlayers) {
        players = new LinkedBlockingQueue<>();
        playersThreads = new LinkedBlockingQueue<>();
        this.matchId = matchId;
        this.matchMaxPlayers = numOfPlayers;
        hasStarted = false;
        hasBeenForcefullyEnded = false;
        controller = null;

        actionQueue = new LinkedBlockingQueue<>();

    }

    /**
     * Generates a new game model via the instance manager and starts the internal match executor loop
     * to begin processing player actions.
     *
     * @throws IOException if there's an error during the creation of the match components
     */
    private synchronized void start() throws IOException {
        ArrayList<String> playerNames = new ArrayList<>(players.stream().map(VirtualClient::getNickname).collect(Collectors.toCollection(ArrayList::new)));
        DTONotifier notifier = new DTONotifier();
        GameModel model = new ModelInstancesManager(notifier).createGame(playerNames);

        players.forEach(c -> c.subscribeToNotifier(notifier)); //adds all listeners
        model.startGame();

        controller = new GameController(model, notifier);
        hasStarted = true;
        players.forEach(c -> c.setController(controller));
        players.forEach(c -> c.setActionQueue(actionQueue));
        players.forEach(c -> playersThreads.add(new Thread(c, c.getNickname())));
        playersThreads.forEach(Thread::start);

        matchExecutorThread = new Thread(this::matchLoop, "MatchExecutorThread-" + matchId);
        matchExecutorThread.start();

    }

    /**
     * Checks if a player with the given nickname is currently part of this match.
     *
     * @param nickname The player's nickname
     * @return true if the player is found inside the match, false otherwise
     */
    public boolean hasPlayer(String nickname) {
        return players.stream().anyMatch(client -> client.getNickname().equals(nickname));
    }

    /**
     * The main execution loop of the match.
     * Constantly polls the {@link #actionQueue action queue} for incoming player commands, executes them against the game controller,
     * and handles any exceptions.
     * Automatically attempts to save the leaderboard whenever the game reaches its natural end.
     */
    private void matchLoop() {
        while (!hasEnded()) {
            try {
                ControllerCommand action = actionQueue.take();

                try {
                    action.execute(controller);

                } catch (IllegalGameActionException | IndexOutOfBoundsException e) {
                    System.err.println("An error occurred while trying to perform " + action.getNickname() + " action: ");
                    e.printStackTrace();
                    System.err.println("Faulty action: "+action.getRequest()+", index: "+action.getIndex());

                    players.stream()
                            .filter(c -> c.getNickname().equals(action.getNickname()))
                            .findFirst().ifPresent(offender -> offender.sendErrorMessage(e.getMessage()));

                } catch (Exception e) {
                    System.err.println("Critical error stemming from: " + action.getNickname() + " action: ");
                    e.printStackTrace();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        if (controller != null && controller.isGameFinished()) {
            try {
                LeaderboardDAO.saveLeaderboard(controller.getModel().getLeaderboard());
            } catch (Exception e) {
                System.err.println("Something went wrong with leaderboard saving request, please check if mySql server is online");
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the unique match ID assigned to this session
     */
    public int getMatchId() {
        return matchId;
    }

    /**
     * Attempts to add a new {@link VirtualClient client} to the match.
     * If the match reaches its target capacity with this new player, the game will start automatically!
     *
     * @param c the client trying to join
     * @throws IllegalStateException if the match is full or has already started
     * @throws IOException           if there is an issue establishing the initial game model for the clients
     */
    public synchronized void addPlayer(VirtualClient c) throws IllegalStateException, IOException {
        if (isFull() || hasStarted) throw new IllegalStateException();
        players.add(c);
        if (isFull()) start();
    }

    /**
     * Removes the player from the match and gracefully interrupts their internal thread, if present.
     *
     * @param nickname the nickname of the player that needs to be removed.
     * @return true if the player was found and removed from the match.
     */
    public synchronized boolean removePlayer(String nickname) {
        VirtualClient client = players.stream().filter(c -> c.getNickname().equals(nickname)).findFirst()
                .orElse(null);
        if (client == null) return false;
        playersThreads.stream().filter(t -> t.getName().equals(nickname)).findFirst().ifPresent(Thread::interrupt);
        return players.remove(client);
    }

    /**
     * Forcefully terminates the match, disconnecting all active players
     * and stopping all background threads tied to this session.
     *
     * @return true if the match was currently running and has now been killed, false otherwise.
     */
    public synchronized boolean killMatch() {
        hasBeenForcefullyEnded = true;
        if (hasStarted) {
            players.forEach(VirtualClient::closeConnection);
            playersThreads.forEach(Thread::interrupt);
            if (matchExecutorThread != null) {
                matchExecutorThread.interrupt();
            }
            return true;
        }
        return false;
    }

    /**
     * @return true if the match has reached its maximum player capacity
     */
    public boolean isFull() {
        return players.size() == matchMaxPlayers;
    }

    /**
     * @return the maximum number of players allowed in this match
     */
    public int getMatchMaxPlayers() {
        return matchMaxPlayers;
    }

    /**
     * @return the current number of players waiting or actively playing in the match
     */
    public int getMatchNumOfPlayers() {
        return players.size();
    }

    /**
     * @return true if the game has officially begun
     */
    public boolean hasStarted() {
        return hasStarted;
    }

    /**
     * @return true if the game has reached its natural conclusion or was forcefully ended early
     */
    public boolean hasEnded() {
        return hasBeenForcefullyEnded || (controller != null && controller.isGameFinished());
    }
}
