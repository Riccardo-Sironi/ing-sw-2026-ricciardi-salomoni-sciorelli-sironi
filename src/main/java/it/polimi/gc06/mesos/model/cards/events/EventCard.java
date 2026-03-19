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
     * this method is used by the other event classes to resolve the event card,
     * in particular it is used to apply the effects of the event card on the player that has chosen to resolve it.
     *
     * @param player the player that is resolving the event
     */
    public abstract void resolveEvent(Player player);

    /**
     * this method is used to know if the card is an event card or not.
     *
     * @return true, because this is the EventCard class.
     */
    @Override
    public boolean isEventCard() {
        return true;
    }
}