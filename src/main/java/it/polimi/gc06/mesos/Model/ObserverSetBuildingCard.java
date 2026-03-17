package it.polimi.gc06.mesos.Model;

public class ObserverSetBuildingCard extends BuildingCard implements DrawObserver {

    @Override
    public int getPrestigeGain(Player owner) {
        return super.getPrestigeGain(owner);
    }

    @Override
    public int getFoodCost() {
        return super.getFoodCost();
    }

    @Override
    public Era getEra() {
        return super.getEra();
    }

    public ObserverSetBuildingCard(Era era, int foodCost, int prestigeGain) {
        super(era, foodCost, prestigeGain);
    }

    @Override
    public void update(Player player) {
        if (player.hasCompletedSet()) {
            player.addFoodTokens(5);
            player.decreaseCharactersSets();
        }
    }

}
