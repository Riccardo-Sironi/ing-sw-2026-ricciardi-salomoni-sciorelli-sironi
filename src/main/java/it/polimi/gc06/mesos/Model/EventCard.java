package it.polimi.gc06.mesos.Model;

public abstract class EventCard extends TribeCard{

    private String eventType;
    private int era;

    //CONSTRUCTOR
    public EventCard(String eventType, int era){
        this.eventType = eventType;
        this.era = era;
    }

    //EVENT TYPE
    protected String getEventType(){
        return this.eventType;
    }

    //EVENT ERA
    protected int getEra(){
        return this.era;
    }

    //RESOLVE
    public abstract void resolveEvent(Player player);
}
