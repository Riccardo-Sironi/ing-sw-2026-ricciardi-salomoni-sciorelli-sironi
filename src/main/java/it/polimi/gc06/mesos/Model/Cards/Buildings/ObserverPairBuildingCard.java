package it.polimi.gc06.mesos.Model.Cards.Buildings;

import it.polimi.gc06.mesos.Model.Era;
import it.polimi.gc06.mesos.Model.GameTurnManager.DrawObserver;
import it.polimi.gc06.mesos.Model.Player;

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
