package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;

public class ChooseCardTileEffect implements TileEffect {

    private int numOfTopCards;
    private int numOfBottomCards;

    /**
     * For testing purpose only!
     */
    public ChooseCardTileEffect(int numOfTopCards, int numOfBottomCards) {
        this.numOfTopCards = numOfTopCards;
        this.numOfBottomCards = numOfBottomCards;
    }

    public ChooseCardTileEffect(){
        numOfBottomCards = 0;
        numOfTopCards = 0;
    }

    /**
     * NumOfTopCards setter.This method should be use only once during initialization.
     * @param numOfTopCards the number of cards the player can draw from the top row.
     */
    public void setNumOfTopCards(int numOfTopCards) {
        this.numOfTopCards = numOfTopCards;
    }

    /**
     * NumOfBottomCards setter. This method should be use only once during initialization.
     * @param numOfBottomCards the number of cards the player can draw from the bottom row.
     */
    public void setNumOfBottomCards(int numOfBottomCards) {
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

    /**
     * this method is used to accept a visitor that will visit the effect and apply
     * the contextualized action of the visitor.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(TileEffectVisitor visitor) {visitor.visit(this);}
}
