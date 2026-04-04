package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.Player;

public class ShamanVisitor extends CardVisitor {

    private final Player player;

    /**
     * this method should not be used in this implementation.
     */
    public ShamanVisitor(Player player) {
        this.player = player;
    }

    /**
     * this method is used to update the number of SHAMAN stars to the player characters sets.
     *
     * @param card the card that will be visited.
     */
    @Override
    public void visit(ShamanCard card) {
        player.increaseShamanStars(card.getStars());
    }

}
