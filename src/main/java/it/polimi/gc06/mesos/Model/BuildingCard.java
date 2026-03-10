package it.polimi.gc06.mesos.Model;

public class BuildingCard {
    private Era era;
    private int foodCost;
    private int prestigeGain;
    private BuildingEffect effect;

    public BuildingCard(Era era, int foodCost, int prestigeGain, BuildingEffect effect) {
        this.era = era;
        this.foodCost = foodCost;
        this.prestigeGain = prestigeGain;
        this.effect = effect;
    }

    protected boolean applyEffect(Player player){
        return true;
    }

    public Era getEra() {
        return era;
    }

    public int getFoodCost() {
        return foodCost;
    }

    public int getPrestigeGain() {
        return prestigeGain;
    }
}
