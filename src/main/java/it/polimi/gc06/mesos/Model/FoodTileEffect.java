package it.polimi.gc06.mesos.Model;

public class FoodTileEffect implements TileEffect{

    private final int numFood;

    /**
     * Applies the effect to the player.
     *
     * @param numFood represents the number of food tokens added to the player.
     * @throws IllegalArgumentException {@inheritDoc}
     */
    FoodTileEffect(int numFood) throws IllegalArgumentException{
        if(numFood <= 0) throw new IllegalArgumentException();
        this.numFood = numFood;
    }

    /**
     * Adds numFood food tokens to the player if the general supply has enough.
     *
     * @param player the player to which the effect will be applied.
     * @param context the current GameModel.
     * @return whether the effect has been applied successfully to the player
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public boolean execute(Player player, GameModel context) throws IllegalArgumentException{
        if(player == null || context == null) throw new IllegalArgumentException();

        int availableFood = context.removeUpToFromGeneralFoodSupply(numFood);
        if(availableFood > 0) player.addFood(availableFood);
        else return false;

        return true;
    }
}
