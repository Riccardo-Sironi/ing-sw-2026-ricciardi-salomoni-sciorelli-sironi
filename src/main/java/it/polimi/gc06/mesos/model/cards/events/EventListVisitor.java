package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.cards.CardVisitor;

import java.util.ArrayList;

public class EventListVisitor extends CardVisitor {
    private final ArrayList<EventCard> events;

    public EventListVisitor(ArrayList<EventCard> events) {
        this.events = events;
    }

    /**
     * this method visits a RitualEvent and adds it to the event list.
     *
     * @param ritual the RitualEvent to be added.
     */
    @Override
    public void visit(RitualEvent ritual) {
        events.add(ritual);
    }


    /**
     * this method visits a RitualEvent and adds it to the event list.
     *
     * @param sustenance the SustenanceEvent to be added.
     */
    @Override
    public void visit(SustenanceEvent sustenance) {
        events.add(sustenance);
    }


    /**
     * this method visits a RitualEvent and adds it to the event list.
     *
     * @param hunt the HuntEvent to be added.
     */
    @Override
    public void visit(HuntEvent hunt) {
        events.add(hunt);
    }


    /**
     * this method visits a RitualEvent and adds it to the event list.
     *
     * @param paintings the PaintingsEvent to be added.
     */
    @Override
    public void visit(PaintingsEvent paintings) {
        events.add(paintings);
    }
}
