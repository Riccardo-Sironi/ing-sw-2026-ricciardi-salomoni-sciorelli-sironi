package it.polimi.gc06.mesos.Model;

public class ChooseCardTileEffect implements TileEffect{

    private final int numOfTopCards;
    private final int numOfBottomCards;

    public ChooseCardTileEffect(int numOfTopCards, int numOfBottomCards) {
        this.numOfTopCards = numOfTopCards;
        this.numOfBottomCards = numOfBottomCards;
    }

    /**
     * Let the player draw the specified number of card from the top/bottom row.
     *
     * @param player the player placed on the tile.
     * @param context the current GameModel.
     * @return if the effect has been applied successfully
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    @Override
    public boolean execute(Player player, GameModel context) throws IllegalArgumentException{
        if(player == null || context == null) throw new IllegalArgumentException();
        //To do: drawing cards logic, could we use integer as player attribute?
        return false;
    }
}
