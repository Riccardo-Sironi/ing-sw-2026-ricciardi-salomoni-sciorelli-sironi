package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.cards.characters.*;

import java.util.ArrayList;

public class EventListVisitor implements TribeCardVisitor {
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

    @Override
    public void visit(HunterCard card) {
    }

    @Override
    public void visit(ShamanCard card) {
    }

    @Override
    public void visit(ArtistCard card) {
    }

    @Override
    public void visit(BuilderCard card) {
    }

    @Override
    public void visit(InventorCard card) {
    }

    @Override
    public void visit(GathererCard card) {
    }

    @Override
    public void visit(TribeCard card) {
    }
}
