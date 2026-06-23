package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.model.Player;

/**
 * An observer interface defining an entity that monitors a player's card draws
 * and updates their state accordingly.
 */
public interface DrawObserver {

    /**
     * Updates the observer when a player draws a card.
     *
     * @param player The player who performed the draw action.
     */
    void update(Player player);
}