package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.*;

public class CharactersPresenceVisitor extends CardVisitor {
    boolean areThereCardsInTopRow = false;


    public CharactersPresenceVisitor() {
    }

    public boolean areCharactersPresent() {
        return this.areThereCardsInTopRow;
    }

    @Override
    public void visit(Card card) {
        super.visit(card);
    }

    @Override
    public void visit(TribeCard card) {
        super.visit(card);
    }

    @Override
    public void visit(EventCard card) {
        super.visit(card);
    }

    @Override
    public void visit(CharacterCard card) {
        areThereCardsInTopRow = true;
    }

    @Override
    public void visit(BuildingCard card) {
        super.visit(card);
    }

    @Override
    public void visit(RitualEvent ritual) {
        super.visit(ritual);
    }

    @Override
    public void visit(SustenanceEvent sustenance) {
        super.visit(sustenance);
    }

    @Override
    public void visit(HuntEvent hunt) {
        super.visit(hunt);
    }

    @Override
    public void visit(PaintingsEvent paintings) {
        super.visit(paintings);
    }

    @Override
    public void visit(HunterCard card) {
        areThereCardsInTopRow = true;
    }

    @Override
    public void visit(ShamanCard card) {
        areThereCardsInTopRow = true;
    }

    @Override
    public void visit(ArtistCard card) {
        areThereCardsInTopRow = true;
    }

    @Override
    public void visit(BuilderCard card) {
        areThereCardsInTopRow = true;
    }

    @Override
    public void visit(InventorCard card) {
        areThereCardsInTopRow = true;
    }

    @Override
    public void visit(GathererCard card) {
        areThereCardsInTopRow = true;
    }
}
