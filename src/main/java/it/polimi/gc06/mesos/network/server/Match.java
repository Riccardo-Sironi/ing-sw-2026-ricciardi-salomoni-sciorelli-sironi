package it.polimi.gc06.mesos.network.server;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.InstancesManager.ModelInstancesManager;
import it.polimi.gc06.mesos.network.socket.ClientManager;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Match {
    // TODO Fix and add RMI ClientManager
    private final BlockingQueue<ClientManager> players;
    private final BlockingQueue<Thread> playersThreads;
    public static final int MAX_PLAYERS = 5;
    public static final int MIN_PLAYERS = 2;
    private final int matchId;
    private final int matchMaxPlayers;
    private boolean hasStarted;
    private boolean hasBeenForcefullyEnded;
    private GameController controller;

    public Match(int matchId, int numOfPlayers) {
        players = new LinkedBlockingQueue<>();
        playersThreads = new LinkedBlockingQueue<>();
        this.matchId = matchId;
        this.matchMaxPlayers = numOfPlayers;
        hasStarted = false;
        hasBeenForcefullyEnded = false;
        controller = null;
    }

    private synchronized void start() throws IOException {
        GameModel model = new ModelInstancesManager().createGame(
                players.stream().map(ClientManager::getNickname).toList());
        model.startGame(); //TODO: is necessary?
        controller = new GameController(model);
        hasStarted = true;
        players.forEach(c -> c.setController(controller));
        players.forEach(c -> playersThreads.add(new Thread(c, c.getNickname())));
        playersThreads.forEach(Thread::start);
    }

    public int getMatchId() {
        return matchId;
    }

    public synchronized void addPlayer(ClientManager c) throws IllegalStateException, IOException {
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
        ClientManager client = players.stream().filter(c -> c.getNickname().equals(nickname)).findFirst()
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
            players.forEach(ClientManager::closeConnection);
            playersThreads.forEach(Thread::interrupt);
            return true;
        }
        return false;
    }

    public synchronized boolean isFull() {
        return players.size() == matchMaxPlayers;
    }

    public int getMatchMaxPlayers() {
        return matchMaxPlayers;
    }

    public synchronized int getMatchNumOfPlayers() {
        return players.size();
    }

    public synchronized boolean hasStarted() {
        return hasStarted;
    }

    public synchronized boolean hasEnded() {
        return hasBeenForcefullyEnded || (controller != null && controller.isGameFinished());
    }
}
