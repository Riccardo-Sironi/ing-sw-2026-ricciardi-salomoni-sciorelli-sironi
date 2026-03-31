package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;

public class FoodTileEffect implements TileEffect {

    private int numFood;
    private ModifierBuildingCard foodBonusCard;

    public FoodTileEffect(){
        numFood = 0;
    }

    public void setNumFood(int numFood) {
        this.numFood = numFood;
    }

    public void setRegistry(ModifierBuildingsRegistry registry){
        foodBonusCard = registry.get(ModifierBuildingRegistryKey.TILE_FOOD_BONUS);
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
