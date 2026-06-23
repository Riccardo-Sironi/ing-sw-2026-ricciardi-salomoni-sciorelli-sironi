package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.model.Player;

/**
 * A subject interface for the Observer pattern, responsible for managing
 * entities that need to react to card draw events.
 */
public interface DrawSubject {

    /**
     * Adds an observer to the list of entities monitoring draw events.
     *
     * @param observer The observer to add.
     */
    void addObserver(DrawObserver observer);

    /**
     * Removes an observer from the list of entities monitoring draw events.
     *
     * @param observer The observer to remove.
     */
    void removeObserver(DrawObserver observer);

    /**
     * Notifies all registered observers that a player has interacted with building cards.
     *
     * @param player The player involved in the action.
     */
    void notifyObserverBuildings(Player player);
}