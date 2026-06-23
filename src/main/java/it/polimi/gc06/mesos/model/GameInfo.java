package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.model.gameTurnManager.DrawObserver;

/**
 * An interface providing read-only access to specific aggregated game statistics,
 * mainly used by players to evaluate their standing compared to others.
 */
public interface GameInfo {

    /**
     * Retrieves the maximum number of shaman stars currently held by any player.
     *
     * @return The highest number of shaman stars among all players.
     */
    int getMaxStars();

    /**
     * Retrieves the minimum number of shaman stars currently held by any player.
     *
     * @return The lowest number of shaman stars among all players.
     */
    int getMinStars();

    /**
     * Adds a draw observer to the game board.
     *
     * @param observer The observer to add.
     */
    void addObserver(DrawObserver observer);

    /**
     * Retrieves the number of players that currently hold the maximum amount of shaman stars.
     *
     * @return The count of players tied for the most shaman stars.
     */
    int getNumPlayerMaxStars();
}