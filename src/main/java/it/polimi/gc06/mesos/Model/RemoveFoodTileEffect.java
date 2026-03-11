package it.polimi.gc06.mesos.Model;

public class RemoveFoodTileEffect implements TileEffect{

    /**
     * Remove one food from the player, if it has none it removes two prestige.
     *
     * @param player the player to which the effect will be applied.
     * @param context the current GameModel.
     * @return whether the effect has been applied successfully to the player
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public boolean execute(Player player, GameModel context) throws IllegalArgumentException{
        if(player == null || context == null) throw new IllegalArgumentException();
        try{
            player.removeFoodTokens(1);
        }catch(IllegalStateException e){
            player.removePrestigeTokens(2);
        }
        return true;
    }
}
