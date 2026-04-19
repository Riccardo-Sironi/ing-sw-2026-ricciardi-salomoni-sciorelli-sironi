package it.polimi.gc06.mesos.network.server;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.InstancesManager.ModelInstancesManager;
import it.polimi.gc06.mesos.network.socket.commands.Command;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Match {
    private final BlockingQueue<VirtualClient> players;
    private final BlockingQueue<Thread> playersThreads;

    private final BlockingQueue<Command> actionQueue;
    private Thread matchExecutorThread;


    public static final int MAX_PLAYERS = 5;
    public static final int MIN_PLAYERS = 2;
    private final int matchId;
    private final int matchMaxPlayers;
    private volatile boolean hasStarted;
    private volatile boolean hasBeenForcefullyEnded;
    private GameController controller;

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

    private synchronized void start() throws IOException {
        GameModel model = new ModelInstancesManager().createGame(
                players.stream().map(VirtualClient::getNickname).toList());
        model.startGame(); //TODO: is necessary?
        controller = new GameController(model);
        hasStarted = true;
        players.forEach(c -> c.setController(controller));
        players.forEach(c -> c.setActionQueue(actionQueue));
        players.forEach(c -> playersThreads.add(new Thread(c, c.getNickname())));
        playersThreads.forEach(Thread::start);

        matchExecutorThread = new Thread(this::matchLoop, "MatchExecutorThread-" + matchId);
        matchExecutorThread.start();

    }

    private void matchLoop() {
        while (!hasEnded()) {
            try {
                Command action = actionQueue.take();

                try {
                    action.execute(controller);

                } catch (IllegalGameActionException | IndexOutOfBoundsException e) {
                    System.err.println("An error occurred while trying to perform " + action.getNickname() + " action: " + e.getMessage());

                    // Cerchiamo il client "colpevole"
                    players.stream()
                            .filter(c -> c.getNickname().equals(action.getNickname()))
                            .findFirst().ifPresent(offender -> offender.sendErrorMessage(e.getMessage()));

                } catch (Exception e) {
                    System.err.println("Critical error stemming from: " + action.getNickname() + " action");
                    e.printStackTrace();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Interrotto in caso di killMatch()
                break;
            }
        }
    }

    public int getMatchId() {
        return matchId;
    }

    public synchronized void addPlayer(VirtualClient c) throws IllegalStateException, IOException {
        if (isFull() || hasStarted) throw new IllegalStateException();
        players.add(c);
        if (isFull()) start();
    }

    /**
     * Removes the player from the match and interrupts internal ClientManager
     * threads, if present.
     *
     * @param nickname the nickname of the player that needs to be removed.
     * @return true if the player was in the match player list.
     */
    public synchronized boolean removePlayer(String nickname) {
        VirtualClient client = players.stream().filter(c -> c.getNickname().equals(nickname)).findFirst()
                .orElse(null);
        if (client == null) return false;
        playersThreads.stream().filter(t -> t.getName().equals(nickname)).findFirst().ifPresent(Thread::interrupt);
        return players.remove(client);
    }

    /**
     * Interrupts each thread of the match.
     * The Threads should do the logout when interrupted.
     *
     * @return true if the match was started.
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

    public boolean isFull() {
        return players.size() == matchMaxPlayers;
    }

    public int getMatchMaxPlayers() {
        return matchMaxPlayers;
    }

    public int getMatchNumOfPlayers() {
        return players.size();
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public boolean hasEnded() {
        return hasBeenForcefullyEnded || (controller != null && controller.isGameFinished());
    }
}
