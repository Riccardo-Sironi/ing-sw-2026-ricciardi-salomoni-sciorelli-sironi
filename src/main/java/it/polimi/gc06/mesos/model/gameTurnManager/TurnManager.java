package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameBoard.TurnOrderTile;

import java.util.List;

public class TurnManager {

    final private List<Player> playersOrder;
    private Player currentPlayer;
    private Phase phase;

    final private TribeCardVisitor cardVisitor;

    ///  The index of the active player in the turn order
    private int activePlayerIndex;

    private int round;

    public TurnManager(List<Player> playersOrder, TurnOrderTile turnOrderTile, int round, List<TileSlot> offerTrack, TribeCardVisitor cardVisitor) {
        this.playersOrder = playersOrder;
        this.currentPlayer = playersOrder.getFirst();
        this.phase = new PlacingTotemPhase();
        this.activePlayerIndex = 0;
        this.round = round;
        this.cardVisitor = cardVisitor;
    }


    /**
     * {@inheritDoc}
     * <p>
     * Sets the current phase of the game. This method should be used to change the phase of the game, and it should be called by the phases themselves when they want to move to the next phase.
     *
     */
    public void setPhase(Phase phase) {

        // TODO: Add throw
        this.phase = phase;
    }


    /**
     * {@inheritDoc}
     * <p>
     * Sets the current round of the game.
     */
    public void setRound(int round) {
        this.round = round;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Sets the current player index in the turn order.
     */
    public void setActivePlayerIndex(int activePlayerIndex) {
        this.activePlayerIndex = activePlayerIndex;
    }


    /**
     * {@inheritDoc}
     *
     * @return the next player in the turn order
     */

    protected Player getNextPlayer() {
        return playersOrder.getFirst();
    }

    /**
     * {@inheritDoc}
     *
     * @return the card visitor
     */
    protected TribeCardVisitor getCardVisitor() {
        return cardVisitor;
    }

    /**
     * {@inheritDoc}
     *
     * @return the active player
     */
    public Player getActivePlayer() {
        return playersOrder.get(activePlayerIndex);
    }

    public void checkIsCurrentPlayer(Player player) throws IllegalAccessError {
        if (!player.equals(currentPlayer)) {
            throw new IllegalAccessError("It's not your turn yet!");
        }
    }

    public void nextTurn() throws IllegalAccessError {

    }

    /**
     * {@inheritDoc}
     *
     * @return the game current phase
     */
    public Phase getPhase() {
        return phase;
    }

    /**
     * {@inheritDoc}
     *
     * @return the current player order
     */
    public List<Player> getPlayersOrder() {
        return playersOrder;
    }

    /**
     * {@inheritDoc}
     *
     * @return the current game round
     */
    public int getRound() {
        return round;
    }

    /**
     * {@inheritDoc}
     *
     * @return the index of the active player in the turn order
     */
    protected int getActivePlayerIndex() {
        return activePlayerIndex;
    }
    

}
