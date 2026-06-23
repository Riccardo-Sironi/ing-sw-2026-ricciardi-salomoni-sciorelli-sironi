package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.dtos.RoundChangeDTO;
import it.polimi.gc06.mesos.model.DTONotifier;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;

import java.util.List;

/**
 * Manages the flow of the game, including player turn order, round tracking,
 * and coordinating state transitions between different game phases.
 */
public class TurnManager {

    final private List<Player> playersOrder;
    private Phase phase;
    private int round;
    private int activePlayerIndex;
    private final ModifierBuildingCard pickFromTopCard;
    private GameModel gameModel;
    private DTONotifier notifier;

    /**
     * Constructs a new TurnManager.
     *
     * @param playersOrder The ordered list of players participating in the game.
     * @param registry The registry containing game rule modifiers.
     * @param notifier The network component used to dispatch DTOs to the clients.
     */
    public TurnManager(List<Player> playersOrder, ModifierBuildingsRegistry registry, DTONotifier notifier) {
        this.playersOrder = playersOrder;
        this.pickFromTopCard = registry.get(ModifierBuildingRegistryKey.PICK_FROM_TOP);
        this.phase = new PlacingTotemPhase();
        this.activePlayerIndex = 0; // this is normally set to 0, it could change in the endOfRoundPhase
        this.round = 1;
        this.gameModel = null;
        this.notifier = notifier;
    }

    /**
     * Sets the current phase of the game.
     * This method should be used to change the phase of the game, and it should be called by the phases themselves when they want to move to the next phase.
     *
     * @param phase The new Phase to set.
     */
    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    /**
     * Sets the current round of the game, notifies the clients, and saves a state snapshot.
     *
     * @param round The new round number.
     */
    public void setRound(int round) {
        notifier.notifyChange(new RoundChangeDTO(round));
        this.round = round;
        gameModel.saveSnapshot();
    }

    /**
     * Retrieves the network notifier.
     *
     * @return The DTONotifier instance.
     */
    public DTONotifier getNotifier() {
        return notifier;
    }

    /**
     * Sets the index of the currently active player.
     *
     * @param activePlayerIndex The index of the active player in the players order list.
     */
    public void setActivePlayerIndex(int activePlayerIndex) {
        this.activePlayerIndex = activePlayerIndex;
    }

    /**
     * Retrieves the active player index.
     *
     * @return The active player index.
     */
    public int getActivePlayerIndex() {
        return activePlayerIndex;
    }

    /**
     * Retrieves the currently active player based on the active player index.
     *
     * @return The active player.
     */
    public Player getActivePlayer() {
        return playersOrder.get(activePlayerIndex);
    }

    /**
     * Retrieves the current game phase.
     *
     * @return The current Phase instance.
     */
    public Phase getPhase() {
        return phase;
    }

    /**
     * Retrieves the ordered list of players.
     *
     * @return The list of players defining turn order.
     */
    public List<Player> getPlayersOrder() {
        return playersOrder;
    }

    /**
     * Retrieves the current game round.
     *
     * @return The current round number.
     */
    public int getRound() {
        return round;
    }

    /**
     * Retrieves the specific modifier card that allows picking from the top row.
     *
     * @return The ModifierBuildingCard for top row picking.
     */
    public ModifierBuildingCard getPickFromTopCard() {
        return pickFromTopCard;
    }

    /**
     * Retrieves the game model.
     *
     * @return The GameModel instance.
     */
    public GameModel getGameModel() {
        return gameModel;
    }

    /**
     * Sets the game model for the turn manager.
     *
     * @param model The game model to set.
     */
    public void setGameModel(GameModel model) {
        this.gameModel = model;
    }

    /**
     * Sets the notifier. This is typically used when restoring a game.
     *
     * @param notifier The DTONotifier instance.
     */
    public void setNotifier(DTONotifier notifier) {
        this.notifier = notifier;
    }

    /**
     * Forces the specified state onto the turnManager. Used to recover games on server crash.
     *
     * @param round The round number to restore.
     * @param activePlayerIndex The active player index to restore.
     * @param phase The phase to restore.
     */
    public void forceState(int round, int activePlayerIndex, Phase phase) {
        this.round = round;
        this.activePlayerIndex = activePlayerIndex;
        this.phase = phase;
    }
}