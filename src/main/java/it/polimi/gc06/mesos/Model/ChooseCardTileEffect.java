package it.polimi.gc06.mesos.Model;

public class ChooseCardTileEffect implements TileEffect{

    private final int numOfTopCards;
    private final int numOfBottomcards;

    public ChooseCardTileEffect(int numOfTopCards, int numOfBottomcards) {
        this.numOfTopCards = numOfTopCards;
        this.numOfBottomcards = numOfBottomcards;
    }

    /**
     * Let the player draw .
     *
     * @param player the player placed on the tile.
     * @param context the current GameModel.
     * @return if the effect has been applied succesfuly
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    @Override
    public boolean execute(Player player, GameModel context) throws IllegalArgumentException{
        if(player == null || context == null) throw new IllegalArgumentException();
        //To do: drawing cards logic, could we use intger as player attirbute?
        return false;
    }
}
