package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.gameTurnManager.DrawObserver;
import it.polimi.gc06.mesos.model.Player;

public class ObserverPairBuildingCard extends BuildingCard implements DrawObserver {

    /**
     * Update if player has completed a pair, if so he gets 2 food tokens and decrease the number of pairs completed by 1.
     *
     * @param player the player to update.
     */
    @Override
    public void update(Player player) {
        if (player.hasCompletedPair()) {
            player.addFoodTokens(2);
            player.decreaseInventorPair();
        }
    }

    /**
     * this method is used to accept a visitor that will visit the card and apply
     * the effects of the card on the player that has chosen to resolve it.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(CardVisitor visitor) {visitor.visit(this);}

    @Override
    public boolean equals(Object o){
        return getClass() == o.getClass();
    }
}
