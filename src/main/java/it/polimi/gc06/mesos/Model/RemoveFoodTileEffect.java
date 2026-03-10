package it.polimi.gc06.mesos.Model;

public class RemoveFoodTileEffect implements TileEffect{

    @Override
    public boolean execute(Player player, GameModel context) throws IllegalArgumentException{
        if(player == null || context == null) throw new IllegalArgumentException();
        try{

            player.removeFoodTokens(1);
            context.addToGeneralFoodSupply(1);

        }catch(IllegalArgumentException e){

            player.removePrestige(2);

        }
        return true;
    }
}
