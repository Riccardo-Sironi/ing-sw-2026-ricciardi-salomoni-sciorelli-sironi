package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.HuntEvent;
import it.polimi.gc06.mesos.model.cards.events.PaintingsEvent;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;

public interface TribeCardVisitor {

    void visit(RitualEvent ritual);

    void visit(SustenanceEvent sustenance);

    void visit(HuntEvent hunt);

    void visit(PaintingsEvent paintings);

    void visit(HunterCard card);

    void visit(ShamanCard card);

    void visit(ArtistCard card);

    void visit(BuilderCard card);

    void visit(InventorCard card);

    void visit(GathererCard card);

    // Fallback/Default
    void visit(TribeCard card);
}
