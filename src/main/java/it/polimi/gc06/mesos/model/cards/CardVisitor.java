package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.cards.buildings.*;
import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.*;

/**
 * The base abstract visitor for all cards in the game.
 * It implements empty or fallback traversal methods for every specific card type,
 * allowing concrete visitors to override only the methods they need.
 */
public abstract class CardVisitor {
    /**
     * This method visits a generic Card.
     *
     * @param card The Card to be visited.
     */
    public void visit(Card card) {
    }

    /**
     * This method visits a generic TribeCard.
     *
     * @param card The TribeCard to be visited.
     */
    public void visit(TribeCard card) {
    }

    /**
     * This method visits a generic EventCard.
     *
     * @param card The EventCard to be visited.
     */
    public void visit(EventCard card) {
    }

    /**
     * This method visits a generic CharacterCard.
     *
     * @param card The CharacterCard to be visited.
     */
    public void visit(CharacterCard card) {
    }

    /**
     * This method visits a generic BuildingCard.
     *
     * @param card The BuildingCard to be visited.
     */
    public void visit(BuildingCard card) {
    }

    /**
     * This method visits a RitualEvent.
     * Falls back to the generic EventCard visit logic.
     *
     * @param ritual The RitualEvent to be visited.
     */
    public void visit(RitualEvent ritual) {
        visit((EventCard) ritual);
    }

    /**
     * This method visits a SustenanceEvent.
     * Falls back to the generic EventCard visit logic.
     *
     * @param sustenance The Sustenance Event to be visited.
     */
    public void visit(SustenanceEvent sustenance) {
        visit((EventCard) sustenance);
    }

    /**
     * This method visits a HuntEvent.
     * Falls back to the generic EventCard visit logic.
     *
     * @param hunt The HuntEvent to be visited.
     */
    public void visit(HuntEvent hunt) {
        visit((EventCard) hunt);
    }

    /**
     * This method visits a PaintingsEvent.
     * Falls back to the generic EventCard visit logic.
     *
     * @param paintings The PaintingsEvent to be visited.
     */
    public void visit(PaintingsEvent paintings) {
        visit((EventCard) paintings);
    }

    /**
     * This method visits a HunterCard.
     * Falls back to the generic CharacterCard visit logic.
     *
     * @param card The HunterCard to be visited.
     */
    public void visit(HunterCard card) {
        visit((CharacterCard) card);
    }

    /**
     * This method visits a ShamanCard.
     * Falls back to the generic CharacterCard visit logic.
     *
     * @param card The ShamanCard to be visited.
     */
    public void visit(ShamanCard card) {
        visit((CharacterCard) card);
    }

    /**
     * This method visits an ArtistCard.
     * Falls back to the generic CharacterCard visit logic.
     *
     * @param card The ArtistCard to be visited.
     */
    public void visit(ArtistCard card) {
        visit((CharacterCard) card);
    }

    /**
     * This method visits a BuilderCard.
     * Falls back to the generic CharacterCard visit logic.
     *
     * @param card The BuilderCard to be visited.
     */
    public void visit(BuilderCard card) {
        visit((CharacterCard) card);
    }

    /**
     * This method visits an InventorCard.
     * Falls back to the generic CharacterCard visit logic.
     *
     * @param card The InventorCard to be visited.
     */
    public void visit(InventorCard card) {
        visit((CharacterCard) card);
    }

    /**
     * This method visits a GathererCard.
     * Falls back to the generic CharacterCard visit logic.
     *
     * @param card The GathererCard to be visited.
     */
    public void visit(GathererCard card) {
        visit((CharacterCard) card);
    }

    /**
     * Visits an EndGameBuildingCard.
     * Falls back to the generic BuildingCard visit logic.
     *
     * @param building The EndGameBuildingCard to be visited.
     */
    public void visit(EndGameBuildingCard building) {
        visit((BuildingCard) building);
    }

    /**
     * Visits a ModifierBuildingCard.
     * Falls back to the generic BuildingCard visit logic.
     *
     * @param building The ModifierBuildingCard to be visited.
     */
    public void visit(ModifierBuildingCard building) {
        visit((BuildingCard) building);
    }

    /**
     * Visits an ObserverSetBuildingCard.
     * Falls back to the generic BuildingCard visit logic.
     *
     * @param building The ObserverSetBuildingCard to be visited.
     */
    public void visit(ObserverSetBuildingCard building) {
        visit((BuildingCard) building);
    }

    /**
     * Visits an ObserverPairBuildingCard.
     * Falls back to the generic BuildingCard visit logic.
     *
     * @param building The ObserverPairBuildingCard to be visited.
     */
    public void visit(ObserverPairBuildingCard building) {
        visit((BuildingCard) building);
    }
}