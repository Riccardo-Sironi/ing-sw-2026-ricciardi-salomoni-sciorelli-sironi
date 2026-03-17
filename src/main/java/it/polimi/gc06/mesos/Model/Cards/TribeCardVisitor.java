package it.polimi.gc06.mesos.Model.Cards;

import it.polimi.gc06.mesos.Model.Cards.Characters.*;
import it.polimi.gc06.mesos.Model.Cards.Events.HuntEvent;
import it.polimi.gc06.mesos.Model.Cards.Events.PaintingsEvent;
import it.polimi.gc06.mesos.Model.Cards.Events.RitualEvent;
import it.polimi.gc06.mesos.Model.Cards.Events.SustenanceEvent;

public interface TribeCardVisitor {

    // Events
    void visit(RitualEvent ritual);

    void visit(SustenanceEvent sustenance);

    void visit(HuntEvent hunt);

    void visit(PaintingsEvent paintings);

    // Characters
    void visit(HunterCard card);

    void visit(ShamanCard card);

    void visit(ArtistCard card);

    void visit(BuilderCard card);

    void visit(InventorCard card);

    void visit(GathererCard card);

    // Fallback/Default
    void visit(TribeCard card);
}
