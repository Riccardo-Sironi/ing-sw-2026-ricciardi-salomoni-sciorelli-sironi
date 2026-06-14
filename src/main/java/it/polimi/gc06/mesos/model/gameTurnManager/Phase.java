package it.polimi.gc06.mesos.model.gameTurnManager;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PlacingTotemPhase.class, name = "placing_totem"),
        @JsonSubTypes.Type(value = OfferResolutionPhase.class, name = "offer_resolution"),
        @JsonSubTypes.Type(value = EventResolutionPhase.class, name = "event_resolution"),
        @JsonSubTypes.Type(value = EndOfRoundPhase.class, name = "end_of_round")
})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public abstract class Phase {

    public Phase() {
    }

    /**
     * This method attempts to place a player's totem on a specified tile slot.
     * By default, this action is not permitted and must be overridden by specific phases.
     *
     * @param turnManager the turn manager orchestrating the game flow.
     * @param player the player attempting to place the totem.
     * @param tileSlot the target tile slot for the totem.
     * @param board the game board.
     * @throws IllegalPhaseActionException if the totem cannot be placed during the current phase.
     */
    public void placeTotem(TurnManager turnManager, Player player, TileSlot tileSlot, Board board) throws IllegalPhaseActionException {
        throw new IllegalPhaseActionException("You can't place a totem in this phase!");
    }

    /**
     * This method starts the resolution of the offer track for a specific player.
     * By default, this action is not permitted and must be overridden by specific phases.
     *
     * @param turnManager the turn manager orchestrating the game flow.
     * @param player the player resolving their offer.
     * @param tileSlot the tile slot chosen by the player.
     * @throws IllegalPhaseActionException if offer resolution cannot be started during the current phase.
     */
    public void startPlayerOfferResolution(TurnManager turnManager, Player player, TileSlot tileSlot) throws IllegalPhaseActionException {
        throw new IllegalPhaseActionException("You can't resolve an offer in this phase!");
    }

    /**
     * This method resolves the event cards currently active on the board.
     * By default, this action is not permitted and must be overridden by specific phases.
     *
     * @param turnManager the turn manager orchestrating the game flow.
     * @param board the game board.
     * @throws IllegalPhaseActionException if events cannot be resolved during the current phase.
     */
    public void resolveEvent(TurnManager turnManager, Board board) throws IllegalPhaseActionException {
        throw new IllegalPhaseActionException("You can't resolve an event in this phase!");
    }

    /**
     * This method executes the end-of-round routine, such as moving cards, updating the board, and advancing the game.
     * By default, this action is not permitted and must be overridden by specific phases.
     *
     * @param turnManager the turn manager orchestrating the game flow.
     * @param board the game board.
     * @param gameModel the main game model containing overall state.
     * @throws IllegalPhaseActionException if the round cannot be ended during the current phase.
     */
    public void endOfRound(TurnManager turnManager, Board board, GameModel gameModel) throws IllegalPhaseActionException {
        throw new IllegalPhaseActionException("You can't end the round in this phase!");
    }

    /**
     * This method attempts to pick a CharacterCard from the top row of the board.
     * By default, this action is not permitted and must be overridden by specific phases.
     *
     * @param turnManager the turn manager orchestrating the game flow.
     * @param player the player picking the card.
     * @param card the CharacterCard to be picked.
     * @param board the game board.
     * @throws IllegalPhaseActionException if a card cannot be picked during the current phase.
     * @throws IllegalArgumentException    if the provided arguments are invalid.
     */
    public void pickCardFromTop(TurnManager turnManager, Player player, CharacterCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException {
        throw new IllegalPhaseActionException("You cannot draw yet!");
    }

    /**
     * This method attempts to pick a CharacterCard from the bottom row of the board.
     * By default, this action is not permitted and must be overridden by specific phases.
     *
     * @param turnManager the turn manager orchestrating the game flow.
     * @param player the player picking the card.
     * @param card the CharacterCard to be picked.
     * @param board the game board.
     * @throws IllegalPhaseActionException if a card cannot be picked during the current phase.
     * @throws IllegalArgumentException if the provided arguments are invalid.
     */
    public void pickCardFromBottom(TurnManager turnManager, Player player, CharacterCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException {
        throw new IllegalPhaseActionException("You cannot draw yet!");
    }

    /**
     * This method attempts to acquire a BuildingCard from the top row of the board.
     * By default, this action is not permitted and must be overridden by specific phases.
     *
     * @param turnManager the turn manager orchestrating the game flow.
     * @param player the player acquiring the building card.
     * @param card the BuildingCard to be acquired.
     * @param board the game board.
     * @throws IllegalPhaseActionException if a building cannot be acquired during the current phase.
     * @throws IllegalArgumentException if the provided arguments are invalid.
     */
    public void pickCardFromTop(TurnManager turnManager, Player player, BuildingCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException {
        throw new IllegalPhaseActionException("You cannot draw yet!");
    }

    /**
     * This method attempts to acquire a BuildingCard from the bottom row of the board.
     * By default, this action is not permitted and must be overridden by specific phases.
     *
     * @param turnManager the turn manager orchestrating the game flow.
     * @param player the player acquiring the building card.
     * @param card the BuildingCard to be acquired.
     * @param board the game board.
     * @throws IllegalPhaseActionException if a building cannot be acquired during the current phase.
     * @throws IllegalArgumentException if the provided arguments are invalid.
     */
    public void pickCardFromBottom(TurnManager turnManager, Player player, BuildingCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException {
        throw new IllegalPhaseActionException("You cannot draw yet!");
    }

    /**
     * This method attempts to pick an EventCard from the bottom row of the board.
     * Players generally cannot pick event cards; they are resolved automatically.
     * By default, this action throws an exception.
     *
     * @param turnManager the turn manager orchestrating the game flow.
     * @param player the player attempting to pick the card.
     * @param card the EventCard in question.
     * @param board the game board.
     * @throws IllegalPhaseActionException always, as players cannot pick event cards directly.
     * @throws IllegalArgumentException if the provided arguments are invalid.
     */
    public void pickCardFromBottom(TurnManager turnManager, Player player, EventCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException {
        throw new IllegalPhaseActionException("You can't pick Event Cards during the Offer Resolution Phase!");
    }

    /**
     * This method attempts to pick an EventCard from the top row of the board.
     * Players generally cannot pick event cards; they are resolved automatically.
     * By default, this action throws an exception.
     *
     * @param turnManager the turn manager orchestrating the game flow.
     * @param player the player attempting to pick the card.
     * @param card the EventCard in question.
     * @param board the game board.
     * @throws IllegalPhaseActionException always, as players cannot pick event cards directly.
     * @throws IllegalArgumentException if the provided arguments are invalid.
     */
    public void pickCardFromTop(TurnManager turnManager, Player player, EventCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException {
        throw new IllegalPhaseActionException("You can't pick Event Cards during the Offer Resolution Phase!");
    }

    /**
     * This method allows a player to skip their card picking action.
     * By default, this action is not permitted and must be overridden by specific phases.
     *
     * @param turnManager the turn manager orchestrating the game flow.
     * @param player the player attempting to skip the pick.
     * @throws IllegalPhaseActionException if skipping is not allowed in the current phase.
     */
    public void skipPick(TurnManager turnManager, Player player) throws IllegalPhaseActionException {
        throw new IllegalPhaseActionException("You can't skip top pick in this phase!");
    }

    /**
     * This method checks whether a player currently has the right to skip a card picking action.
     * By default, this action is not permitted and must be overridden by specific phases.
     *
     * @param player the player requesting to skip.
     * @param board the game board.
     * @return true if the player has the right to skip, false otherwise.
     * @throws IllegalPhaseActionException if the check cannot be performed in the current phase.
     */
    public boolean checkForRightToSkip(Player player, Board board) throws IllegalPhaseActionException{
        throw new IllegalPhaseActionException("You can't skip top pick in this phase!");
    }
}