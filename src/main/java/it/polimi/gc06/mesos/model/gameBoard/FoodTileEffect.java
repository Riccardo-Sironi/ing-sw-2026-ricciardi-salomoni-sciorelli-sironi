package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;

public class FoodTileEffect implements TileEffect {

    private final int numFood;
    
    FoodTileEffect(int numFood) throws IllegalArgumentException {
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

        player.addFoodTokens(numFood);
    }
}
