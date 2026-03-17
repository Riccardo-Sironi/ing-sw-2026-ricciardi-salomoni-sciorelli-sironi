package it.polimi.gc06.mesos.Model.Cards.Events;

import it.polimi.gc06.mesos.Model.Cards.TribeCard;
import it.polimi.gc06.mesos.Model.Era;
import it.polimi.gc06.mesos.Model.Player;

public abstract class EventCard extends TribeCard {
    private final boolean hasPriority;

    //CONSTRUCTOR
    public EventCard(Era era, boolean hasPriority) {
        super(era);
        this.hasPriority = hasPriority;
    }

    public boolean hasPriority() {
        return hasPriority;
    }

    //RESOLVE
    public abstract void resolveEvent(Player player);

    // we need this to initialize the bottom row of the board
    @Override
    public boolean isEventCard() {
        return true;
    }

}