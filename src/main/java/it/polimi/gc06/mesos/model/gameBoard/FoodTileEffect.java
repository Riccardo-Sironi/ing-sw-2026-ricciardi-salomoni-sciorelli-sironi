package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;

public class FoodTileEffect implements TileEffect {

    private final int numFood;
    private final ModifierBuildingCard foodBonusCard;
    
    FoodTileEffect(int numFood, ModifierBuildingCard foodBonusCard) throws IllegalArgumentException {
        this.foodBonusCard = foodBonusCard;
        if (numFood <= 0) throw new IllegalArgumentException();
        this.numFood = numFood;
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
