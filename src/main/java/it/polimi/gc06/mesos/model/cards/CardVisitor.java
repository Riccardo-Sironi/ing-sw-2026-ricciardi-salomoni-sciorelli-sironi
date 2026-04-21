package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.cards.buildings.*;
import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.*;

public abstract class CardVisitor {
    public void visit(Card card) {
    }

    public void visit(TribeCard card) {
    }

    public void visit(EventCard card) {
    }

    public void visit(CharacterCard card) {
    }

    public void visit(BuildingCard card) {
    }

    public void visit(RitualEvent ritual) {
        visit((EventCard) ritual);
    }

    public void visit(SustenanceEvent sustenance) {
        visit((EventCard) sustenance);
    }

    public void visit(HuntEvent hunt) {
        visit((EventCard) hunt);
    }

    public void visit(PaintingsEvent paintings) {
        visit((EventCard) paintings);
    }

    public void visit(HunterCard card) {
        visit((CharacterCard) card);
    }

    public void visit(ShamanCard card) {
        visit((CharacterCard) card);
    }

    public void visit(ArtistCard card) {
        visit((CharacterCard) card);
    }

    public void visit(BuilderCard card) {
        visit((CharacterCard) card);
    }

    public void visit(InventorCard card) {
        visit((CharacterCard) card);
    }

    public void visit(GathererCard card) {
        visit((CharacterCard) card);
    }

    public void visit(EndGameBuildingCard building) {
        visit((BuildingCard) building);
    }

    public void visit(ModifierBuildingCard building) {
        visit((BuildingCard) building);
    }

    public void visit(ObserverSetBuildingCard building) {
        visit((BuildingCard) building);
    }

    public void visit(ObserverPairBuildingCard building) {
        visit((BuildingCard) building);
    }
}