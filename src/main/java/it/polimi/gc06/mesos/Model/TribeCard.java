package it.polimi.gc06.mesos.Model;

public abstract class TribeCard implements Card {
    Era era = Era.ERA_I;

    protected abstract boolean isEventCard();

    protected Era getEra() {
        return era;
    };
}
