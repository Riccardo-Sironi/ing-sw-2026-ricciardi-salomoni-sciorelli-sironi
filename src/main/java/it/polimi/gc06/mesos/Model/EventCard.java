package it.polimi.gc06.mesos.Model;

import java.util.ArrayList;

public abstract class EventCard extends TribeCard {
    private final boolean hasPriority;

    //CONSTRUCTOR
    public EventCard(Era era, boolean hasPriority) {
        super(era);
        this.hasPriority = hasPriority;
    }

    protected boolean hasPriority() {
        return hasPriority;
    }

    //RESOLVE
    protected abstract void resolveEvent(Player player);

    // we need this to initialize the bottom row of the board
    @Override
    public boolean isEventCard() {
        return true;
    }

}