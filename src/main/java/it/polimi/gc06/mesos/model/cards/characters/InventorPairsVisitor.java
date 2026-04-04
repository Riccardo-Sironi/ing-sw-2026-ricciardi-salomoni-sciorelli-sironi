package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.Player;

public class InventorPairsVisitor extends CardVisitor {

    private final Player player;

    public InventorPairsVisitor(Player player) {
        this.player = player;
    }
    /**
     * this method is used to update the number of pairs of the icon of the card to the player.
     *
     * @param card the card that will be visited.
     */
    @Override
    public void visit(InventorCard card) {
        player.increaseInventorPairs(card.getIcon());
    }

}
