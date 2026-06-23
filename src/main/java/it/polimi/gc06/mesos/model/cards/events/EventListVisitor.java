package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.cards.CardVisitor;

import java.util.ArrayList;

/**
 * A visitor that collects specific event cards into a provided list.
 * It is used to filter or group events from a larger collection of cards.
 */
public class EventListVisitor extends CardVisitor {
    private final ArrayList<EventCard> events;

    /**
     * Constructs an EventListVisitor with the target list to populate.
     *
     * @param events The list where visited event cards will be stored.
     */
    public EventListVisitor(ArrayList<EventCard> events) {
        this.events = events;
    }

    /**
     * This method visits a RitualEvent and adds it to the event list.
     *
     * @param ritual The RitualEvent to be added.
     */
    @Override
    public void visit(RitualEvent ritual) {
        events.add(ritual);
    }

    /**
     * This method visits a SustenanceEvent and adds it to the event list.
     *
     * @param sustenance The SustenanceEvent to be added.
     */
    @Override
    public void visit(SustenanceEvent sustenance) {
        events.add(sustenance);
    }

    /**
     * This method visits a HuntEvent and adds it to the event list.
     *
     * @param hunt The HuntEvent to be added.
     */
    @Override
    public void visit(HuntEvent hunt) {
        events.add(hunt);
    }

    /**
     * This method visits a PaintingsEvent and adds it to the event list.
     *
     * @param paintings The PaintingsEvent to be added.
     */
    @Override
    public void visit(PaintingsEvent paintings) {
        events.add(paintings);
    }
}