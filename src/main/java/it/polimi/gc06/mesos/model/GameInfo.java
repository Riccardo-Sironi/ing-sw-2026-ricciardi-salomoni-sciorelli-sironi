package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.model.gameTurnManager.DrawObserver;

public interface GameInfo {
    int getMaxStars();

    int getMinStars();

    void addObserver(DrawObserver observer);

    int getNumPlayerMaxStars();
}