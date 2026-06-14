package it.polimi.gc06.mesos.view.smallModel;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.network.leaderboard.Score;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The client-side representation of the Game Model (Small Model).
 * It holds the local state of the game, including the cards on the board,
 * the offer track, the player's info, and their opponents' info.
 * This model is updated dynamically by receiving DTO changes from the server.
 */
public class SmallModel implements Serializable {

    //cards
    private final ArrayList<Card> topRow;
    private final ArrayList<Card> bottomRow;
    private final ArrayList<Card> topBuildings;
    private final ArrayList<Card> bottomBuildings;
    private final ArrayList<PlayerView> turnOrderTile;
    private final ArrayList<TileSlotView> offerTrack;

    //state
    private Era era;
    private String phase;
    private int tribeDeckSize;
    private int round;
    private int topDrawNum;
    private int bottomDrawNum;
    private boolean isActive;
    private boolean canSkip;

    //context
    private PlayerView player;
    private final List<PlayerView> opponents; //is immutable
    transient private final ArrayList<Score> leaderboard;

    private int maxPlayers;

    transient private ModelListener listener;

    private String systemMessage;

    /**
     * Constructs a new SmallModel instance for a specific player.
     * Initializes all the standard collections and zeroes out integers.
     *
     * @param nickname the primary player's network nickname
     */
    public SmallModel(String nickname) {
        this.topRow = new ArrayList<>();
        this.bottomRow = new ArrayList<>();
        this.topBuildings = new ArrayList<>();
        this.bottomBuildings = new ArrayList<>();
        this.turnOrderTile = new ArrayList<>();
        this.offerTrack = new ArrayList<>();
        this.era = null;
        this.phase = null;
        this.tribeDeckSize = -1;
        this.round = -1;
        this.topDrawNum = -1;
        this.bottomDrawNum = -1;
        this.player = new PlayerView(nickname, null);
        this.opponents = new ArrayList<>();
        this.isActive = false;
        this.canSkip = false;
        this.leaderboard = new ArrayList<>();
        this.listener = null;
        this.maxPlayers = -1;
        this.systemMessage = "";
    }

    /**
     * Factory to completely copy a small model reference.
     *
     * @param other the small model reference
     */
    public void copy(SmallModel other) {
        topRow.clear();
        topRow.addAll(other.topRow);
        bottomRow.clear();
        bottomRow.addAll(other.bottomRow);
        topBuildings.clear();
        topBuildings.addAll(other.topBuildings);
        bottomBuildings.clear();
        bottomBuildings.addAll(other.bottomBuildings);
        turnOrderTile.clear();
        turnOrderTile.addAll(other.turnOrderTile);
        offerTrack.clear();
        offerTrack.addAll(other.offerTrack);
        if(other.era != null) this.era = other.era;
        if(other.phase != null) this.phase = other.phase;
        this.tribeDeckSize = other.tribeDeckSize;
        this.round = other.round;
        this.topDrawNum = other.topDrawNum;
        this.bottomDrawNum = other.bottomDrawNum;
        this.isActive = other.isActive;
        this.canSkip = other.canSkip;
        this.maxPlayers = other.maxPlayers;
        this.player = null;
        if(other.player != null){
            PlayerView pv = new PlayerView(other.player.getNickname(),other.getPlayer().getColor());
            pv.copy(other.player);
            this.player = pv;
        }
        opponents.clear();
        for (PlayerView op : other.opponents) {
            PlayerView pv = new PlayerView(op.getNickname(),op.getColor());
            pv.copy(op);
            this.opponents.add(pv);
        }
        this.leaderboard.clear();
        if(other.leaderboard != null) this.leaderboard.addAll(other.leaderboard);
        this.listener = null;
    }

    /**
     * Checks if it is currently this player's turn to perform an action.
     *
     * @return true if the player is the active one, false otherwise
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Sets whether it's this player's turn contextually.
     *
     * @param active true if the player should become active, false otherwise
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Sets the System's message (Model notifications, Errors or information in general)
     *
     * @param systemMessage the message to be displayed on the client interface
     */
    public void setSystemMessage(String systemMessage) {
        this.systemMessage = systemMessage;
    }

    /**
     * Getter for the System's message (Model notifications, Errors or information in general)
     *
     * @return systemMessage the message to be displayed on the client interface
     */
    public String getSystemMessage() {
        return this.systemMessage;
    }


    /**
     * Checks whether the game has transitioned into the endgame scenario
     * by verifying if the finalized leaderboard has been populated.
     *
     * @return true if the game is in endgame logic, false otherwise
     */
    public boolean isEndgame() {
        return !leaderboard.isEmpty();
    }

    /**
     * Checks whether the active player has the authorization to skip
     * certain sub-actions in the current phase context.
     *
     * @return true if skipping is allowed, false otherwise
     */
    public boolean isCanSkip() {
        return canSkip;
    }

    /**
     * Toggles whether the active player is authorized to skip their phase actions.
     *
     * @param canSkip the skipping authorization boolean flag
     */
    public void setCanSkip(boolean canSkip) {
        this.canSkip = canSkip;
    }

    /**
     * Retrieves the remaining size of the unrevealed tribe deck on the board.
     *
     * @return the number of character cards left to draw from the deck
     */
    public int getTribeDeckSize() {
        return tribeDeckSize;
    }

    /**
     * Sets the remaining size of the unrevealed tribe deck on the board.
     *
     * @param tribeDeckSize the number of character cards left
     */
    public void setTribeDeckSize(int tribeDeckSize) {
        this.tribeDeckSize = tribeDeckSize;
    }

    /**
     * Retrieves the index tracking the completed number of rounds.
     *
     * @return the current round's numerical index
     */
    public int getRound() {
        return round;
    }

    /**
     * Updates the index tracking the specific gameplay round limits.
     *
     * @param round the current round's index
     */
    public void setRound(int round) {
        this.round = round;
    }

    /**
     * Retrieves the current chronological game Era standard context.
     *
     * @return the current game Era enum value
     */
    public Era getEra() {
        return era;
    }

    /**
     * Updates the chronological Era standard context of the game.
     *
     * @param era the specific game Era enum value
     */
    public void setEra(Era era) {
        this.era = era;
    }

    /**
     * Retrieves the number of allowed drawing actions from the top row
     * remaining for the current player's chosen tile.
     *
     * @return the number of permissible top card draws
     */
    public int getTopDrawNum() {
        return topDrawNum;
    }

    /**
     * Sets the number of allowed drawing actions from the top row
     * dictated by the tile logic effects.
     *
     * @param topDrawNum the top draw constraints setup size
     */
    public void setTopDrawNum(int topDrawNum) {
        this.topDrawNum = topDrawNum;
    }

    /**
     * Retrieves the number of allowed drawing actions from the bottom row
     * remaining for the current player's chosen tile.
     *
     * @return the number of permissible bottom card draws
     */
    public int getBottomDrawNum() {
        return bottomDrawNum;
    }

    /**
     * Sets the number of allowed drawing actions from the bottom row
     * dictated by the tile logic effects.
     *
     * @param bottomDrawNum the bottom draw constraints setup size
     */
    public void setBottomDrawNum(int bottomDrawNum) {
        this.bottomDrawNum = bottomDrawNum;
    }

    /**
     * Re-instantiates and overrides the local main player's view logic component.
     *
     * @param nickname the specific unique nickname value
     * @param color    the model enumeration tracking color specifics
     */
    public void setPlayer(String nickname, Color color) {
        this.player = new PlayerView(nickname, color);
    }

    /**
     * Retrieves the local main player's view logic component
     *
     * @return the main player's PlayerView
     */
    public PlayerView getPlayer() {
        return player;
    }

    /**
     * Incorporates a distinct competitor's external logic component into the opponents' array tracker.
     *
     * @param pv the structured opponent player view component
     */
    public void addOpponent(PlayerView pv) {
        opponents.add(pv);
    }

    /**
     * Accesses the array tracker aggregating the external opponent player logic pieces.
     *
     * @return the immutable external opponent structural list
     */
    public List<PlayerView> getOpponents() {
        return opponents;
    }

    /**
     * Extracts the top row structural array tracking the visible cards.
     *
     * @return the top row board list component
     */
    public ArrayList<Card> getTopRow() {
        return topRow;
    }

    /**
     * Extracts the bottom row structural array tracking the visible cards.
     *
     * @return the bottom row board list component
     */
    public ArrayList<Card> getBottomRow() {
        return bottomRow;
    }

    /**
     * Extracts the structural array tracking the buildings currently available on the top sequence.
     *
     * @return the top row building layer board list component
     */
    public ArrayList<Card> getTopBuildings() {
        return topBuildings;
    }

    /**
     * Extracts the structural array tracking the buildings presently exposed over the bottom sequence logic.
     *
     * @return the bottom row building layer board list component
     */
    public ArrayList<Card> getBottomBuildings() {
        return bottomBuildings;
    }

    /**
     * Extracts the sequence, detailing the resolution order.
     *
     * @return the turn order mapping list abstraction
     */
    public ArrayList<PlayerView> getTurnOrderTile() {
        return turnOrderTile;
    }

    /**
     * Extracts the logical board slot array tracking offer tile placements
     *
     * @return the array component referencing interactive tile slots
     */
    public ArrayList<TileSlotView> getOfferTrack() {
        return offerTrack;
    }

    /**
     * Assimilates a resolved leaderboard structure within the framework contexts.
     *
     * @param leaderboard the endpoint structure resolving player placements and logic scores
     */
    public void setLeaderboard(Collection<Score> leaderboard) {
        this.leaderboard.addAll(leaderboard);
    }

    /**
     * Ascertains the finalized leaderboard framework.
     *
     * @return the list representing ultimate score data sequences
     */
    public ArrayList<Score> getLeaderboard() {
        return leaderboard;
    }

    /**
     * Retrieves the current phase
     *
     * @return the sequence label depicting phase components
     */
    public String getPhase() {
        return phase;
    }

    /**
     * Sets the current phase
     *
     * @param phase the generic timeline classification logic wrapper string
     */
    public void setPhase(String phase) {
        this.phase = phase;
    }

    /**
     * Attaches an internal view observer object intercepting structural alterations across sequences.
     *
     * @param listener the observer component listening to UI abstractions
     */
    public void setListener(ModelListener listener) {
        this.listener = listener;
    }

    /**
     * Directly publishes abstraction changes sequentially onto any active framework endpoints.
     *
     * @param dto the encapsulated variable subset containing differential changes
     */
    public void notifyListener(SmallModelEditor dto) {
        if (this.listener != null) {
            this.listener.update(dto);
        }
    }

    /**
     * Determines the maximum boundary constraints on standard player elements in the generic logic game.
     *
     * @return the threshold identifying acceptable participant parameters
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Reconfigures and restructures player thresholds matching the parameters in the lobby configuration scenarios.
     *
     * @param maxPlayers the limiting cap regarding distinct game individuals
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * For debugging.
     */
    @Override
    public String toString() {
        return "SmallModel{" +
                "era=" + era +
                ", phase='" + phase + '\'' +
                ", tribeDeckSize=" + tribeDeckSize + ",\n" +
                ", round=" + round + ",\n" +
                ", topDrawNum=" + topDrawNum + ",\n" +
                ", bottomDrawNum=" + bottomDrawNum + ",\n" +
                ", isActive=" + isActive + ",\n" +
                ", canSkip=" + canSkip + ",\n" +
                ", maxPlayers=" + maxPlayers + ",\n" +
                ", player=" + player + ",\n" +
                ", opponents=" + opponents.toString() + ",\n" +
                "  topRow=" + topRow.toString() + ",\n" +
                "  bottomRow=" + bottomRow.toString() + ",\n" +
                ", topBuildings=" + topBuildings.toString() + ",\n" +
                ", bottomBuildings=" + bottomBuildings.toString() + ",\n" +
                ", turnOrderTile=" + turnOrderTile.toString() + ",\n" +
                ", offerTrack=" + offerTrack.toString() + "\n" +
                '}';
    }
}
