package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;

public class ChooseCardTileEffect implements TileEffect {

    private final int numOfTopCards;
    private final int numOfBottomCards;

    public ChooseCardTileEffect(int numOfTopCards, int numOfBottomCards) {
        this.numOfTopCards = numOfTopCards;
        this.numOfBottomCards = numOfBottomCards;
    }

    /**
     * Let the player draw the specified number of card from the top/bottom row.
     *
     * @param player the player to which the effect will be applied to.
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public void execute(Player player) throws IllegalArgumentException {
        if (player == null) throw new IllegalArgumentException();
        player.setTopDrawNum(numOfTopCards);
        player.setBottomDrawNum(numOfBottomCards);
    }
}
