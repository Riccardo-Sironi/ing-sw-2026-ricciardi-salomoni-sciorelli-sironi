package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.events.HuntEvent;
import it.polimi.gc06.mesos.model.cards.events.PaintingsEvent;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.Player;

public class InventorPairsVisitor implements TribeCardVisitor {

    private final Player player;

    public InventorPairsVisitor(Player player) {
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
     * this method should not be used in this implementation.
     */
    @Override
    public void visit(HunterCard card) {

    }

    /**
     * this method should not be used in this implementation.
     */
    @Override
    public void visit(ShamanCard card) {

    }

    /**
     * this method should not be used in this implementation.
     */
    @Override
    public void visit(ArtistCard card) {

    }

    /**
     * this method should not be used in this implementation.
     */
    @Override
    public void visit(BuilderCard card) {

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

    /**
     * this method should not be used in this implementation.
     */
    @Override
    public void visit(GathererCard card) {

    }

    /**
     * this method should not be used in this implementation.
     */
    @Override
    public void visit(TribeCard card) {

    }
}
