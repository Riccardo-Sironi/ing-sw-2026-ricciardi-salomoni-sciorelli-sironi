package it.polimi.gc06.mesos.server;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.InstancesManager.ModelInstancesManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Match {

    private final BlockingQueue<ClientManager> players;
    public static final int MAX_PLAYERS = 5;
    public static final int MIN_PLAYERS = 2;
    private final int matchId;
    private final int numOfPlayers;
    private boolean hasStarted;

    public Match(int matchId, int numOfPlayers){
        players = new LinkedBlockingQueue<>();
        this.matchId = matchId;
        this.numOfPlayers = numOfPlayers;
        hasStarted = false;
    }

    private synchronized void start() throws IOException {
        GameModel model = new ModelInstancesManager().createGame(
                players.stream().map(ClientManager::getNickname).toList());
        model.startGame(); //TODO: is necessary?
        GameController controller = new GameController(model);
        hasStarted = true;
        players.forEach(c -> c.setController(controller));
        players.parallelStream().forEach(c -> new Thread(c).start());
    }

    public int getMatchId() {
        return matchId;
    }

    public synchronized List<String> getPlayers(){
        return players.stream().map(ClientManager::getNickname).toList();
    }

    public synchronized void addPlayer(ClientManager c) throws IllegalStateException, IOException{
        if(isFull()) throw new IllegalStateException();
        players.add(c);
        if(isFull()) start();
    }

    public synchronized boolean isFull(){
        return players.size() == numOfPlayers;
    }

    public synchronized boolean hasStarted(){
        return hasStarted;
    }

}
