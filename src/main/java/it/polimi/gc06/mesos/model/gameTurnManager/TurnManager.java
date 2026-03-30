package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;

import java.util.List;

public class TurnManager {

    final private List<Player> playersOrder;
    private Player currentPlayer;
    private Phase phase;

    final private TribeCardVisitor cardVisitor;

    private int round;

    private final ModifierBuildingCard pickFromTopCard;

    public TurnManager(List<Player> playersOrder, TribeCardVisitor cardVisitor, ModifierBuildingCard pickFromTopCard) {
        this.playersOrder = playersOrder;
        this.currentPlayer = playersOrder.getFirst();
        this.pickFromTopCard = pickFromTopCard;
        this.phase = new PlacingTotemPhase();
        this.round = 0;
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
        return playersOrder.getFirst();
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
     * modifier card getter
     *
     * @return the modifier card
     */
    public ModifierBuildingCard getPickFromTopCard() {
        return pickFromTopCard;
    }
}
