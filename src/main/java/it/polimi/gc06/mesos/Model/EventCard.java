package it.polimi.gc06.mesos.Model;

public abstract class EventCard extends TribeCard{
    private EventType eventType;
    private Era era;

    //CONSTRUCTOR
    public EventCard(EventType eventType, Era era){
        this.eventType = eventType;
        this.era = era;
    }

    //EVENT TYPE
    protected EventType getEventType(){
        return this.eventType;
    }

    //RESOLVE
    protected abstract void resolveEvent(Player player, GameModel context);

    // we need this to initialize the bottom row of the board
    @Override
    public boolean isEventCard() {
        return true;
    }

}
