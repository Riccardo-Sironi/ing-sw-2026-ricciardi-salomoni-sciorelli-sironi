package it.polimi.gc06.mesos.Model;

import java.awt.*;

public class EventResolutionVisitor implements TribeCardVisitor {

    // TODO Finire implementazione eventi. Capire cosa passare dentro costruttore, e quale player passare.
    // TODO In teoria qualsiasi player va bene, fintanto che veda lo stato della partita.
    @Override
    public void visit(RitualEvent event) {
        event.resolveEvent(new Player("Ciao", new Color(0, 0, 0)));
    }

    @Override
    public void visit(SustenanceEvent event) {
        event.resolveEvent(new Player("Ciao", new Color(0, 0, 0)));
    }

    @Override
    public void visit(HuntEvent event) {
        event.resolveEvent(new Player("Ciao", new Color(0, 0, 0)));
    }

    @Override
    public void visit(PaintingsEvent event) {
        event.resolveEvent(new Player("Ciao", new Color(0, 0, 0)));
    }

    /* Do nothing as they are not events */
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
