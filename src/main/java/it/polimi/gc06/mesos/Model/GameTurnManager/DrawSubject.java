package it.polimi.gc06.mesos.Model.GameTurnManager;

import it.polimi.gc06.mesos.Model.Player;

public interface DrawSubject {
    void addObserver(DrawObserver observer);

    void removeObserver(DrawObserver observer);

    void notifyObserverBuildings(Player player);

}
