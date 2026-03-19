package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;

public abstract class EventCard extends TribeCard {
    private final boolean lastToBeResolved;

    public EventCard(Era era, boolean lastToBeResolved) {
        super(era);
        this.lastToBeResolved = lastToBeResolved;
    }

    /**
     * This method checks if the event card is the last to be resolved, in particular this should return true
     * only in the case we  are dealing with the sustenance event card or final event card.
     *
     * @return true if the event card is the last to be resolved in the turn, false otherwise
     */
    public boolean isLastToBeResolved() {
        return lastToBeResolved;
    }

    /**
     * TODO : explain
     *
     * @param player the player that has chosen to resolve the event card
     */
    public abstract void resolveEvent(Player player);

    @Override
    public boolean isEventCard() {
        return true;
    }
}