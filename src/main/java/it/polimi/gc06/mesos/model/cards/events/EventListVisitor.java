package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.cards.CardVisitor;

import java.util.ArrayList;

public class EventListVisitor extends CardVisitor {
    private final ArrayList<EventCard> events;

    public EventListVisitor(ArrayList<EventCard> events) {
        this.events = events;
    }

    @Override
    public void visit(RitualEvent ritual) {
        events.add(ritual);
    }

    @Override
    public void visit(SustenanceEvent sustenance) {
        events.add(sustenance);
    }

    @Override
    public void visit(HuntEvent hunt) {
        events.add(hunt);
    }

    @Override
    public void visit(PaintingsEvent paintings) {
        events.add(paintings);
    }
}
