package it.polimi.gc06.mesos.Model;

public abstract class EventCard extends TribeCard{
    private String eventType;
    private Era era;

    //CONSTRUCTOR
    public EventCard(String eventType, Era era){
        this.eventType = eventType;
        this.era = era;
    }

    //EVENT TYPE
    protected String getEventType(){
        return this.eventType;
    }

    //RESOLVE
    public abstract void resolveEvent(Player player, GameModel context);

    // we need this to initialize the bottom row of the board
    @Override
    public boolean isEventCard() {
        return true;
    }

}
