package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.cards.buildings.*;
import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.*;

public abstract class CardVisitor {
    /**
     * This method visits a generic Card.
     *
     * @param card the Card to be visited.
     */
    public void visit(Card card) {
    }

    /**
     * This method visits a generic TribeCard.
     *
     * @param card the TribeCard to be visited.
     */
    public void visit(TribeCard card) {
    }

    /**
     * This method visits a generic EventCard.
     *
     * @param card the EventCard to be visited.
     */
    public void visit(EventCard card) {
    }

    /**
     * This method visits a generic CharacterCard.
     *
     * @param card the CharacterCard to be visited.
     */
    public void visit(CharacterCard card) {
    }

    /**
     * This method visits a generic BuildingCard.
     *
     * @param card the BuildingCard to be visited.
     */
    public void visit(BuildingCard card) {
    }

    /**
     * This method visits a RitualEvent.
     * Falls back to the generic EventCard visit logic.
     *
     * @param ritual the RitualEvent to be visited.
     */
    public void visit(RitualEvent ritual) {
        visit((EventCard) ritual);
    }

    /**
     * This method visits a SustenanceEvent.
     * Falls back to the generic EventCard visit logic.
     *
     * @param sustenance the Sustenance Event to be visited.
     */
    public void visit(SustenanceEvent sustenance) {
        visit((EventCard) sustenance);
    }

    /**
     * This method visits a HuntEvent.
     * Falls back to the generic EventCard visit logic.
     *
     * @param hunt the HuntEvent to be visited.
     */
    public void visit(HuntEvent hunt) {
        visit((EventCard) hunt);
    }

    /**
     * This method visits a PaintingsEvent.
     * Falls back to the generic EventCard visit logic.
     *
     * @param paintings the PaintingsEvent to be visited.
     */
    public void visit(PaintingsEvent paintings) {
        visit((EventCard) paintings);
    }

    /**
     * This method visits a HunterCard.
     * Falls back to the generic CharacterCard visit logic.
     *
     * @param card the HunterCard to be visited.
     */
    public void visit(HunterCard card) {
        visit((CharacterCard) card);
    }

    /**
     * This method visits a HunterCard.
     * Falls back to the generic CharacterCard visit logic.
     *
     * @param card the HunterCard to be visited.
     */
    public void visit(ShamanCard card) {
        visit((CharacterCard) card);
    }

    /**
     * This method visits an ArtistCard.
     * Falls back to the generic CharacterCard visit logic.
     *
     * @param card the ArtistCard to be visited.
     */
    public void visit(ArtistCard card) {
        visit((CharacterCard) card);
    }

    /**
     * This method visits a BuilderCard.
     * Falls back to the generic CharacterCard visit logic.
     *
     * @param card the BuilderCard to be visited.
     */
    public void visit(BuilderCard card) {
        visit((CharacterCard) card);
    }

    /**
     * This method visits a InventorCard.
     * Falls back to the generic CharacterCard visit logic.
     *
     * @param card the InventorCard to be visited.
     */
    public void visit(InventorCard card) {
        visit((CharacterCard) card);
    }

    /**
     * This method visits a GathererCard.
     * Falls back to the generic CharacterCard visit logic.
     *
     * @param card the GathererCard to be visited.
     */
    public void visit(GathererCard card) {
        visit((CharacterCard) card);
    }

    /**
     * Visits an EndGameBuildingCard.
     * Falls back to the generic BuildingCard visit logic.
     *
     * @param building the EndGameBuildingCard to be visited.
     */
    public void visit(EndGameBuildingCard building) {
        visit((BuildingCard) building);
    }

    /**
     * Visits an ModifierBuildingCard.
     * Falls back to the generic BuildingCard visit logic.
     *
     * @param building the ModifierBuildingCard to be visited.
     */
    public void visit(ModifierBuildingCard building) {
        visit((BuildingCard) building);
    }

    /**
     * Visits an ObserverSetBuildingCard.
     * Falls back to the generic BuildingCard visit logic.
     *
     * @param building the ObserverSetBuildingCard to be visited.
     */
    public void visit(ObserverSetBuildingCard building) {
        visit((BuildingCard) building);
    }

    /**
     * Visits an ObserverPairBuildingCard.
     * Falls back to the generic BuildingCard visit logic.
     *
     * @param building the ObserverPairBuildingCard to be visited.
     */
    public void visit(ObserverPairBuildingCard building) {
        visit((BuildingCard) building);
    }
}