package it.polimi.gc06.mesos.Model;

import java.util.ArrayList;

public abstract class EventCard extends TribeCard {
    private final Era era;
    private final boolean hasPriority;

    //CONSTRUCTOR
    public EventCard(Era era, boolean hasPriority) {
        this.era = era;
        this.hasPriority = hasPriority;
    }

    //RESOLVE
    protected abstract void resolveEvent(Player player);

    // we need this to initialize the bottom row of the board
    @Override
    public boolean isEventCard() {
        return true;
    }

}