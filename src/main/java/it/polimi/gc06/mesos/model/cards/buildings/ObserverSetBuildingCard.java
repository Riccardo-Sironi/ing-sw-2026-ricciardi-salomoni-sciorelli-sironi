package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.gameTurnManager.DrawObserver;
import it.polimi.gc06.mesos.model.Player;

public class ObserverSetBuildingCard extends BuildingCard implements DrawObserver {

    @Override
    public void update(Player player) {
        if (player.hasCompletedSet()) {
            player.addFoodTokens(5);
            player.decreaseCharactersSets();
        }
    }

    @Override
    public void accept(BuildingCardVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * this method is used to accept a visitor that will visit the card and apply
     * the effects of the card on the player that has chosen to resolve it.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(CardVisitor visitor) {visitor.visit(this);}

}
