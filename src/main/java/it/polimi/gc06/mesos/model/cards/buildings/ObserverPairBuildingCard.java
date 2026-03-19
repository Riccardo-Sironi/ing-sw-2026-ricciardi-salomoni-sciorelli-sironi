package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.gameTurnManager.DrawObserver;
import it.polimi.gc06.mesos.model.Player;

public class ObserverPairBuildingCard extends BuildingCard implements DrawObserver {


    public ObserverPairBuildingCard(Era era, int foodCost, int prestigeGain) {
        super(era, foodCost, prestigeGain);
    }
    
    @Override
    public void update(Player player) {
        if (player.hasCompletedPair()) {
            player.addFoodTokens(2);
            player.decreaseInventorPair();
        }
    }

}
