package it.polimi.gc06.mesos.controller;

import it.polimi.gc06.mesos.dtos.*;
import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.DTONotifier;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameController{

    private final GameModel model;
    private final DTONotifier notifier;

    public GameController(GameModel model, DTONotifier notifier) {
        this.model = model;
        this.notifier = notifier;
    }

    /**
     * Retrieves the main game model associated with this controller.
     *
     * @return the GameModel instance.
     */
    public GameModel getModel() {
        return model;
    }

    /**
     * Handles the request of tile placement.
     *
     * @param playerNickname the player performing the action.
     * @param tileIndex      the index of the tile where the player wants to be placed.
     * @throws IllegalPhaseActionException if the action is not allowed in the current phase or if the player is trying to perform an action that is not his turn.
     */
    public void handleTotemOfferTilePlacement(String playerNickname, int tileIndex) throws IllegalPhaseActionException {
        TurnManager turnManager = model.getTurnManager();
        Player activePlayer = turnManager.getActivePlayer();

        if (!activePlayer.getNickname().equals(playerNickname)) {
            throw new IllegalPhaseActionException("It is not " + playerNickname + " turn !");
        }

        List<TileSlot> offerTrack = model.getBoard().getOfferTrack();
        if (tileIndex >= offerTrack.size() || tileIndex < 0) {
            throw new IllegalPhaseActionException("Requested tile does not exist !");
        }
        TileSlot tile = offerTrack.get(tileIndex);

        if (tile.getPlayer() != null) {
            throw new IllegalPhaseActionException("The tile is already occupied !");
        }

        turnManager.getPhase().placeTotem(turnManager, activePlayer, tile, model.getBoard());
    }

    /**
     * Handles the request of card picking from bottom row.
     *
     * @param playerNickname the player performing the action.
     * @param cardIndex      the index of the card that the player wants to pick.
     * @throws IllegalPhaseActionException if the player is trying to perform an action that is not his turn.
     * @throws IllegalGameActionException  if the action is not allowed in the current phase.
     * @throws IndexOutOfBoundsException   if the card index is out of bounds.
     */
    public void handleCardPickBottomRow(String playerNickname, int cardIndex) throws IllegalGameActionException, IndexOutOfBoundsException {
        TurnManager turnManager = model.getTurnManager();
        Player activePlayer = turnManager.getActivePlayer();
        if (!activePlayer.getNickname().equals(playerNickname)) {
            throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
        }

        TribeCard card = model.getBoard().getBottomCardFromIndex(cardIndex);

        CardBottomRowControllerVisitor cardPickerVisitor = new CardBottomRowControllerVisitor(turnManager, activePlayer, model);
        card.accept(cardPickerVisitor);
    }

    /**
     * Handles the request of card picking from top row.
     *
     * @param playerNickname the player performing the action.
     * @param cardIndex      the index of the card that the player wants to pick.
     * @throws IllegalPhaseActionException if the player is trying to perform an action that is not his turn.
     * @throws IllegalGameActionException  if the action is not allowed in the current phase or if the player is trying to perform an action that is not his turn.
     * @throws IndexOutOfBoundsException   if the card index is out of bounds.
     */
    public void handleCardPickTopRow(String playerNickname, int cardIndex) throws IllegalGameActionException, IndexOutOfBoundsException {
        TurnManager turnManager = model.getTurnManager();

        Player activePlayer = turnManager.getActivePlayer();
        if (!activePlayer.getNickname().equals(playerNickname)) {
            throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
        }

        TribeCard card = model.getBoard().getTopCardFromIndex(cardIndex);

        CardTopRowControllerVisitor cardPickerVisitor = new CardTopRowControllerVisitor(turnManager, activePlayer, model);
        card.accept(cardPickerVisitor);
    }

    /**
     * Handles the request of building picking from bottom row.
     *
     * @param playerNickname the player performing the action.
     * @param cardIndex      the index of the building that the player wants to pick.
     * @throws IllegalPhaseActionException if the player is trying to perform an action that is not his turn.
     * @throws IllegalGameActionException  if the action is not allowed in the current phase or if the player is trying to perform an action that is not his turn.
     * @throws IndexOutOfBoundsException   if the card index is out of bounds.
     */
    public void handleBuildingPickBottomRow(String playerNickname, int cardIndex) throws IllegalGameActionException, IndexOutOfBoundsException {
        TurnManager turnManager = model.getTurnManager();


        Player activePlayer = turnManager.getActivePlayer();

        if (!activePlayer.getNickname().equals(playerNickname)) {
            throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
        }

        BuildingCard card = model.getBoard().getBottomBuildingFromIndex(cardIndex);
        turnManager.getPhase().pickCardFromBottom(turnManager, activePlayer, card, model.getBoard());
    }

    /**
     * Handles the request of building picking from top row.
     *
     * @param playerNickname the player performing the action.
     * @param cardIndex      the index of the building that the player wants to pick.
     * @throws IllegalPhaseActionException if the player is trying to perform an action that is not his turn.
     * @throws IllegalGameActionException  if the action is not allowed in the current phase or if the player is trying to perform an action that is not his turn.
     * @throws IndexOutOfBoundsException   if the card index is out of bounds.
     */
    public void handleBuildingPickTopRow(String playerNickname, int cardIndex) throws IllegalGameActionException, IndexOutOfBoundsException {
        TurnManager turnManager = model.getTurnManager();
        Player activePlayer = turnManager.getActivePlayer();

        if (!activePlayer.getNickname().equals(playerNickname)) {
            throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
        }

        BuildingCard card = model.getBoard().getTopBuildingFromIndex(cardIndex);
        turnManager.getPhase().pickCardFromTop(turnManager, activePlayer, card, model.getBoard());
    }

    /**
     * Handles the request of skipping the pick phase.
     *
     * @param playerNickname the player performing the action.
     * @throws IllegalGameActionException if the action is not allowed in the current phase or if the player is trying
     * to perform an action that is not his turn.
     */
    public void handlePickSkip(String playerNickname) throws IllegalGameActionException {
        TurnManager turnManager = model.getTurnManager();
        Player activePlayer = turnManager.getActivePlayer();

        if (!activePlayer.getNickname().equals(playerNickname)) {
            throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
        }
        turnManager.getPhase().skipPick(turnManager, activePlayer);
    }

    /**
     * This method is used to handle the request of totem color choice from the player,
     * it updates the model and notifies all clients about the change.
     *
     * @param playerNickname the nickname of the player who is choosing the totem color.
     * @param color          the color chosen by the player for their totem.
     */

    public void handleChooseTotemColor(String playerNickname, Color color) {
        model.getPlayers().stream()
                .filter(player -> player.getNickname().equals(playerNickname))
                .findFirst().ifPresent(p -> p.setPlayerColor(color));

        ChooseTotemColorDTO dto = new ChooseTotemColorDTO(playerNickname, color);
        notifier.notifyChange(dto);

        boolean areAllPlayersReady = model.getPlayers().stream().allMatch(player -> player.getPlayerColor() != null);

        // if all the players have selected di color we send the DTO to start the game
        if (areAllPlayersReady) {
            MesosStartedDTO dto2 = new MesosStartedDTO();
            notifier.notifyChange(dto2);
        }

    }

    /**
     * Checks whether the game has reached the end state.
     *
     * @return true if the game is finished, false otherwise.
     */
    public boolean isGameFinished() {
        return model.getBoard().isEndGame();
    }
}