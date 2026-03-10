package it.polimi.gc06.mesos.Model;

public class FoodTileEffect implements TileEffect{

    private final int numFood;

    FoodTileEffect(int numFood){
        this.numFood = numFood;
    }

    @Override
    public boolean execute(Player player, GameModel context) throws IllegalArgumentException{
        if(player == null || context == null) throw new IllegalArgumentException();

        int availableFood = context.removeUpToFromGeneralFoodSupply(numFood);
        if(availableFood > 0) player.addFood(availableFood);
        else return false;

        return true;
    }
}
