package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.gameExceptions.GameObjectNotFoundException;
import it.polimi.gc06.mesos.model.gameTurnManager.DrawObserver;
import it.polimi.gc06.mesos.model.gameTurnManager.Phase;

public interface GameInfo {
    int getMaxStars();

    int getMinStars();

    void addObserver(DrawObserver observer);

    int getNumPlayerMaxStars();
}