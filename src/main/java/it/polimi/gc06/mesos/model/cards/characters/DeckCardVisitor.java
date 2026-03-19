package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.events.HuntEvent;
import it.polimi.gc06.mesos.model.cards.events.PaintingsEvent;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.Player;

public class DeckCardVisitor implements TribeCardVisitor {

    private final Player player;

    public DeckCardVisitor(Player player) {
        this.player = player;
    }

    /**
     * this method should not be used in this implementation.
     */
    @Override
    public void visit(RitualEvent ritual) {

    }

    /**
     * this method should not be used in this implementation.
     */
    @Override
    public void visit(SustenanceEvent sustenance) {

    }

    /**
     * this method should not be used in this implementation.
     */
    @Override
    public void visit(HuntEvent hunt) {

    }

    /**
     * this method should not be used in this implementation.
     */
    @Override
    public void visit(PaintingsEvent paintings) {

    }

    /**
     * Visitor that adds the card to the owner deck.
     *
     * @param card the characterCard that will be visited.
     */
    @Override
    public void visit(HunterCard card) {
        player.getCharacterDeck().get(CharacterType.HUNTER).add(card);
    }

    /**
     * Visitor that adds the card to the owner deck.
     *
     * @param card the characterCard that will be visited.
     */
    @Override
    public void visit(ShamanCard card) {
        player.getCharacterDeck().get(CharacterType.SHAMAN).add(card);
    }

    /**
     * Visitor that adds the card to the owner deck.
     *
     * @param card the characterCard that will be visited.
     */
    @Override
    public void visit(ArtistCard card) {
        player.getCharacterDeck().get(CharacterType.ARTIST).add(card);
    }

    /**
     * Visitor that adds the card to the owner deck.
     *
     * @param card the characterCard that will be visited.
     */
    @Override
    public void visit(BuilderCard card) {
        player.getCharacterDeck().get(CharacterType.BUILDER).add(card);
    }

    /**
     * Visitor that adds the card to the owner deck.
     *
     * @param card the characterCard that will be visited.
     */
    @Override
    public void visit(InventorCard card) {
        player.getCharacterDeck().get(CharacterType.INVENTOR).add(card);
    }

    /**
     * Visitor that adds the card to the owner deck.
     *
     * @param card the characterCard that will be visited.
     */
    @Override
    public void visit(GathererCard card) {
        player.getCharacterDeck().get(CharacterType.GATHERER).add(card);
    }

    /**
     * this method should not be used in this implementation.
     */
    //default case
    @Override
    public void visit(TribeCard card) {

    }
}
