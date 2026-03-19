package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameBoard.TurnOrderTile;
import it.polimi.gc06.mesos.model.Player;

import java.util.ArrayList;

public class TurnManager {

    final private ArrayList<Player> playersOrder;
    private Phase phase;

    final private TurnOrderTile turnOrderTile;
    final private ArrayList<TileSlot> offerTrack;

    final private TribeCardVisitor cardVisitor;

    ///  The index of the active player in the turn order
    private int activePlayerIndex;

    private int round;

    public TurnManager(ArrayList<Player> playersOrder, Phase phase, TurnOrderTile turnOrderTile, int round, ArrayList<TileSlot> offerTrack, TribeCardVisitor cardVisitor) {
        this.playersOrder = playersOrder;
        this.phase = phase;
        this.turnOrderTile = turnOrderTile;
        this.activePlayerIndex = 0;
        this.round = round;
        this.offerTrack = offerTrack;
        this.cardVisitor = cardVisitor;
    }


    /**
     * {@inheritDoc}
     * <p>
     * Sets the current phase of the game. This method should be used to change the phase of the game, and it should be called by the phases themselves when they want to move to the next phase.
     *
     */
    protected void setPhase(Phase phase) {

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
     * @return the turn order tile of the game
     */
    public TurnOrderTile getTurnOrderTile() {
        return turnOrderTile;
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
    public ArrayList<Player> getPlayersOrder() {
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

    /**
     * {@inheritDoc}
     *
     * @return the offer track of the game
     */
    public ArrayList<TileSlot> getOfferTrack() {
        return offerTrack;
    }

}
