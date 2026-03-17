package it.polimi.gc06.mesos.Model;

public class ObserverPairBuildingCard extends BuildingCard implements DrawObserver {


    public ObserverPairBuildingCard(Era era, int foodCost, int prestigeGain) {
        super(era, foodCost, prestigeGain);
    }

    @Override
    public Era getEra() {
        return super.getEra();
    }

    @Override
    public int getFoodCost() {
        return super.getFoodCost();
    }

    @Override
    public int getPrestigeGain(Player owner) {
        return super.getPrestigeGain(owner);
    }


    @Override
    public void update(Player player) {
        if (player.hasCompletedPair()) {
            player.addFoodTokens(2);
            player.decreaseInventorPair();
        }
    }

}
