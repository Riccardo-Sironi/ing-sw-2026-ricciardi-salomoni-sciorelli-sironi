package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;

import java.util.List;

public class TurnManager {

    final private List<Player> playersOrder;
    private Phase phase;

    private int round;

    private int activePlayerIndex;

    private final ModifierBuildingCard pickFromTopCard;

    private GameModel gameModel;

    public TurnManager(List<Player> playersOrder, ModifierBuildingsRegistry registry) {
        this.playersOrder = playersOrder;
        this.pickFromTopCard = registry.get(ModifierBuildingRegistryKey.PICK_FROM_TOP);
        this.phase = new PlacingTotemPhase();
        this.activePlayerIndex = 0; // this is normally set to 0, it could change in the endOfRoundPhase
        this.round = 0;
        this.gameModel = null;
    }

    /**
     * Sets the current phase of the game. This method should be used to change the phase of the game, and it should be called by the phases themselves when they want to move to the next phase.
     *
     */
    public void setPhase(Phase phase) {

        // TODO: Add throw
        this.phase = phase;
    }

    /**
     *
     * Sets the current round of the game.
     */
    public void setRound(int round) {
        this.round = round;
    }


    /**
     * set the active player index
     *
     * @param activePlayerIndex the index of the active player in the players order list
     */
    public void setActivePlayerIndex(int activePlayerIndex) {
        this.activePlayerIndex = activePlayerIndex;
    }

    /**
     *
     * @return the active player
     */
    public Player getActivePlayer() {
        return playersOrder.get(activePlayerIndex);
    }

    /**
     *
     *
     * @return the game current phase
     */
    public Phase getPhase() {
        return phase;
    }

    /**
     *
     *
     * @return the current player order
     */
    public List<Player> getPlayersOrder() {
        return playersOrder;
    }

    /**
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

    /**
     * Game model getter
     *
     * @return the game model
     */
    public GameModel getGameModel() {
        return gameModel;
    }

    /**
     * This method sets the game model for the turn manager
     *
     * @param model the game model to set.
     */
    public void setGameModel(GameModel model) {
        this.gameModel = model;
    }
}
