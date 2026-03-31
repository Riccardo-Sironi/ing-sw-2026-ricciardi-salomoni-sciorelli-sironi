package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;

public class FoodTileEffect implements TileEffect {

    private int numFood;
    private ModifierBuildingCard foodBonusCard;

    //TODO remove
    @Deprecated
    FoodTileEffect(int numFood, ModifierBuildingCard foodBonusCard) throws IllegalArgumentException {
        this.foodBonusCard = foodBonusCard;
        if (numFood <= 0) throw new IllegalArgumentException();
        this.numFood = numFood;
    }

    public FoodTileEffect(){
        numFood = 0;
    }

    public void setNumFood(int numFood) {
        this.numFood = numFood;
    }

    //TODO implements the registry
    public void setRegistry(ModifierBuildingsRegistry registry){

    }

    /**
     * Adds numFood food tokens to the player.
     *
     * @param player the player to which the effect will be applied.
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public void execute(Player player) throws IllegalArgumentException {
        if (player == null) throw new IllegalArgumentException();

        player.addFoodTokens(player.getBuildingCards().contains(foodBonusCard) ? numFood + 1 : numFood);
    }
}
