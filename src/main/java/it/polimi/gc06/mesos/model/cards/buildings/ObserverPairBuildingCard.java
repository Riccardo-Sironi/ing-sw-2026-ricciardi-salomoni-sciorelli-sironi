package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Era;
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

    @Override
    public void accept(BuildingCardVisitor visitor) {
        visitor.visit(this);
    }
}
