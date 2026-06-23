package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.*;

/**
 * A visitor that determines if at least one character card is present among the visited cards.
 */
public class CharactersPresenceVisitor extends CardVisitor {
    boolean areThereCardsInTopRow = false;


    /**
     * Constructs a new CharactersPresenceVisitor.
     */
    public CharactersPresenceVisitor() {
    }

    /**
     * This method returns whether any character card has been visited by this visitor.
     *
     * @return If at least one character card was found.
     */
    public boolean areCharactersPresent() {
        return this.areThereCardsInTopRow;
    }

    /**
     * This method visits a generic Card, falling back to the superclass logic.
     *
     * @param card The Card to be visited.
     */
    @Override
    public void visit(Card card) {
        super.visit(card);
    }

    /**
     * This method visits a TribeCard, falling back to the superclass logic.
     *
     * @param card The TribeCard to be visited.
     */
    @Override
    public void visit(TribeCard card) {
        super.visit(card);
    }

    /**
     * This method visits an EventCard, falling back to the superclass logic.
     *
     * @param card The EventCard to be visited.
     */
    @Override
    public void visit(EventCard card) {
        super.visit(card);
    }

    /**
     * This method visits a CharacterCard, falling back to the superclass logic.
     *
     * @param card The CharacterCard to be visited.
     */
    @Override
    public void visit(CharacterCard card) {
        areThereCardsInTopRow = true;
    }

    /**
     * This method visits a BuildingCard, falling back to the superclass logic.
     *
     * @param card The BuildingCard to be visited.
     */
    @Override
    public void visit(BuildingCard card) {
        super.visit(card);
    }

    /**
     * This method visits a RitualEvent, falling back to the superclass logic.
     *
     * @param ritual The RitualEvent to be visited.
     */
    @Override
    public void visit(RitualEvent ritual) {
        super.visit(ritual);
    }

    /**
     * This method visits a SustenanceEvent, falling back to the superclass logic.
     *
     * @param sustenance The SustenanceEvent to be visited.
     */
    @Override
    public void visit(SustenanceEvent sustenance) {
        super.visit(sustenance);
    }

    /**
     * This method visits a HuntEvent, falling back to the superclass logic.
     *
     * @param hunt The HuntEvent to be visited.
     */
    @Override
    public void visit(HuntEvent hunt) {
        super.visit(hunt);
    }

    /**
     * This method visits a PaintingsEvent, falling back to the superclass logic.
     *
     * @param paintings The PaintingsEvent to be visited.
     */
    @Override
    public void visit(PaintingsEvent paintings) {
        super.visit(paintings);
    }

    /**
     * This method visits a HunterCard and registers the presence of a character.
     *
     * @param card The HunterCard to be visited.
     */
    @Override
    public void visit(HunterCard card) {
        areThereCardsInTopRow = true;
    }

    /**
     * This method visits a ShamanCard and registers the presence of a character.
     *
     * @param card The ShamanCard to be visited.
     */
    @Override
    public void visit(ShamanCard card) {
        areThereCardsInTopRow = true;
    }

    /**
     * This method visits an ArtistCard and registers the presence of a character.
     *
     * @param card The ArtistCard to be visited.
     */
    @Override
    public void visit(ArtistCard card) {
        areThereCardsInTopRow = true;
    }

    /**
     * This method visits a BuilderCard and registers the presence of a character.
     *
     * @param card The BuilderCard to be visited.
     */
    @Override
    public void visit(BuilderCard card) {
        areThereCardsInTopRow = true;
    }

    /**
     * This method visits an InventorCard and registers the presence of a character.
     *
     * @param card The InventorCard to be visited.
     */
    @Override
    public void visit(InventorCard card) {
        areThereCardsInTopRow = true;
    }

    /**
     * This method visits a GathererCard and registers the presence of a character.
     *
     * @param card The GathererCard to be visited.
     */
    @Override
    public void visit(GathererCard card) {
        areThereCardsInTopRow = true;
    }
}