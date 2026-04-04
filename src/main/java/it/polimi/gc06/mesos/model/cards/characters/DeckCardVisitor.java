package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.Player;

public class DeckCardVisitor extends CardVisitor {

    private final Player player;

    public DeckCardVisitor(Player player) {
        this.player = player;
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

}
