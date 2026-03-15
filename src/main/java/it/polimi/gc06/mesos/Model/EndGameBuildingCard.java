package it.polimi.gc06.mesos.Model;

import java.util.function.ToIntFunction;

public class EndGameBuildingCard extends BuildingCard{

    private final ToIntFunction<Player> prestigeEffect;

    EndGameBuildingCard(Era era,int foodCost, int prestigeGained, ToIntFunction<Player> prestigeEffect){
        super(era,foodCost,prestigeGained);
        this.prestigeEffect = prestigeEffect;
    }

    public int getPrestigeBonus(Player owner) throws IllegalArgumentException{
        if(owner == null) throw new IllegalArgumentException();
        return prestigeEffect.applyAsInt(owner);
    }
}
