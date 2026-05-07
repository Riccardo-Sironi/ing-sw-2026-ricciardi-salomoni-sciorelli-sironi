package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;

import java.util.Objects;

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

    public ChooseCardTileEffect() {
        numOfBottomCards = 0;
        numOfTopCards = 0;
    }

    /**
     * NumOfTopCards setter.This method should be use only once during initialization.
     *
     * @param numOfTopCards the number of cards the player can draw from the top row.
     */
    public void setNumOfTopCards(int numOfTopCards) {
        this.numOfTopCards = numOfTopCards;
    }

    /**
     * NumOfBottomCards setter. This method should be use only once during initialization.
     *
     * @param numOfBottomCards the number of cards the player can draw from the bottom row.
     */
    public void setNumOfBottomCards(int numOfBottomCards) {
        this.numOfBottomCards = numOfBottomCards;
    }

    /**
     * NumOfBottomCards getter.
     *
     * @return numOfBottomCards the number of cards the player can draw from the bottom row.
     */
    public int getNumOfBottomCards() {
        return numOfBottomCards;
    }

    /**
     * NumOfTopCards getter.
     *
     * @return numOfTopCards the number of cards the player can draw from the top row.
     */
    public int getNumOfTopCards() {
        return numOfTopCards;
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
    public void accept(TileEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChooseCardTileEffect that = (ChooseCardTileEffect) o;
        return numOfTopCards == that.numOfTopCards && numOfBottomCards == that.numOfBottomCards;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numOfTopCards, numOfBottomCards);
    }
}
