package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.model.Player;

public interface DrawSubject {
    void addObserver(DrawObserver observer);

    void removeObserver(DrawObserver observer);

    void notifyObserverBuildings(Player player);

}
