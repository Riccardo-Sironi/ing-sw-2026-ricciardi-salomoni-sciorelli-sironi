package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.gameTurnManager.DrawObserver;

/**
 * A building card that actively monitors a player's inventory and automatically
 * triggers a reward whenever the player successfully completes a pair of inventors.
 */
public class ObserverPairBuildingCard extends BuildingCard implements DrawObserver {

    /**
     * Update if player has completed a pair, if so he gets 3 food tokens and decrease the number of pairs completed by 1.
     *
     * @param player The player to update.
     */
    @Override
    public void update(Player player) {
        if (player.getBuildingCards().contains(this)) {
            if (player.hasCompletedPair()) {
                player.addFoodTokens(3);
                player.decreaseInventorPair();
            }
        }
    }

    /**
     * This method is used to accept a visitor that will visit the card and apply
     * the effects of the card on the player that has chosen to resolve it.
     *
     * @param visitor The visitor that will visit the card.
     */
    @Override
    public void accept(CardVisitor visitor) {
        visitor.visit(this);
    }


    /**
     * This method compares this ObserverPairBuildingCard to the specified object.
     * Since all instances of this specific card behave identically,
     * they are considered equal if they are of the exact same class.
     *
     * @param o The reference object with which to compare.
     * @return True if the given object is exactly of the same class, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        return getClass() == o.getClass();
    }
}