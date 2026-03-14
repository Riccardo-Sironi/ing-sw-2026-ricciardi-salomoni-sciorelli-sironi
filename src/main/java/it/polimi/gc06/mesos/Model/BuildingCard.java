package it.polimi.gc06.mesos.Model;

public class BuildingCard {
    private final Era era;
    private final int foodCost;
    private final int prestigeGain;
    private final BuildingEffect effect;

    public BuildingCard(Era era, int foodCost, int prestigeGain, BuildingEffect effect) {
        this.era = era;
        this.foodCost = foodCost;
        this.prestigeGain = prestigeGain;
        this.effect = effect;
    }


    /**
     *
     * @param player
     * @return
     */
    protected boolean applyEffect(Player player){
        // ...
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
