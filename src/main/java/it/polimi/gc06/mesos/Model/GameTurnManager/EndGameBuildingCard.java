package it.polimi.gc06.mesos.Model.GameTurnManager;

import it.polimi.gc06.mesos.Model.Cards.Buildings.BuildingCard;
import it.polimi.gc06.mesos.Model.Era;
import it.polimi.gc06.mesos.Model.Player;

import java.util.function.ToIntFunction;

public class EndGameBuildingCard extends BuildingCard {

    private final ToIntFunction<Player> prestigeEffect;

    EndGameBuildingCard(Era era, int foodCost, int prestigeGained, ToIntFunction<Player> prestigeEffect) {
        super(era, foodCost, prestigeGained);
        this.prestigeEffect = prestigeEffect;
    }

    @Override
    public int getPrestigeGain(Player owner) {
        if (owner == null) throw new IllegalArgumentException();
        return prestigeEffect.applyAsInt(owner) + super.getPrestigeGain(owner);
    }


}
