package it.polimi.gc06.mesos.Model;

import java.util.ArrayList;

public abstract class EventCard extends TribeCard {
    private final Era era;
    private final EventType eventType;

    //CONSTRUCTOR
    public EventCard(EventType eventType, Era era) {
        this.eventType = eventType;
        this.era = era;
    }

    //EVENT TYPE

    /**
     * This method is used to know which type of event card it is.
     *
     * @return the type of event card
     */
    protected EventType getEventType() {
        return this.eventType;
    }

    //RESOLVE
    protected abstract void resolveEvent(Player player, ArrayList<Player> players);

    // we need this to initialize the bottom row of the board
    @Override
    public boolean isEventCard() {
        return true;
    }

}
