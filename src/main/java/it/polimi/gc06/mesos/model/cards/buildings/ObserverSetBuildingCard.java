package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Era;
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

}
