package it.polimi.gc06.mesos.controller;

import it.polimi.gc06.mesos.dtos.*;
import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private final GameModel model;
    private final ArrayList<ModelListener> listeners;

    public GameController(GameModel model) {
        this.model = model;
        this.listeners = new ArrayList<>();
    }

    public GameModel getModel() {
        return model;
    }

    /**
     * Handles the request of tile placement
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

        model.getChangeHandler().registerState();
        turnManager.getPhase().placeTotem(turnManager, activePlayer, tile, model.getBoard());

        TotemMovedDTO dto = new TotemMovedDTO(playerNickname,tileIndex);
        listeners.forEach(l -> l.update(dto));
        model.getChangeHandler().getChanges().forEach(c ->
                listeners.forEach(l -> l.update(c)));
    }

    /**
     * Handles the request of card picking from bottom row.
     *
     * @param playerNickname the player performing the action.
     * @param cardIndex      the index of the card that the player wants to pick.
     * @throws IllegalPhaseActionException if the player is trying to perform an action that is not his turn.
     * @throws IllegalGameActionException  if the action is not allowed in the current phase
     * @throws IndexOutOfBoundsException   if the card index is out of bounds.
     */
    public void handleCardPickBottomRow(String playerNickname, int cardIndex) throws IllegalGameActionException, IndexOutOfBoundsException {
        TurnManager turnManager = model.getTurnManager();
        Player activePlayer = turnManager.getActivePlayer();
        if (!activePlayer.getNickname().equals(playerNickname)) {
            throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
        }

        TribeCard card = model.getBoard().getBottomCardFromIndex(cardIndex);
        PickBottomRowDTO dto = new PickBottomRowDTO(playerNickname,cardIndex); //prepares the dto if needed

        model.getChangeHandler().registerState();
        CardBottomRowControllerVisitor cardPickerVisitor = new CardBottomRowControllerVisitor(turnManager, activePlayer, model);

        card.accept(cardPickerVisitor);

        //if the operation was not successful the notice won't be sent
        listeners.forEach(l -> l.update(dto));
        model.getChangeHandler().getChanges().forEach(c ->
                listeners.forEach(l -> l.update(c)));
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
        PickTopRowDTO dto = new PickTopRowDTO(playerNickname,cardIndex); //prepares the dto if needed

        model.getChangeHandler().registerState();
        CardTopRowControllerVisitor cardPickerVisitor = new CardTopRowControllerVisitor(turnManager, activePlayer, model);

        card.accept(cardPickerVisitor);

        //if the operation was not successful the notice won't be sent
        listeners.forEach(l -> l.update(dto));
        model.getChangeHandler().getChanges().forEach(c ->
                listeners.forEach(l -> l.update(c)));
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
        PickBottomBuildingsDTO dto = new PickBottomBuildingsDTO(playerNickname,cardIndex); //prepares the dto if needed

        model.getChangeHandler().registerState();
        turnManager.getPhase().pickCardFromBottom(turnManager, activePlayer, card, model.getBoard());

        //if the operation was not successful the notice won't be sent
        listeners.forEach(l -> l.update(dto));
        model.getChangeHandler().getChanges().forEach(c ->
                listeners.forEach(l -> l.update(c)));
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
        PickTopBuildingsDTO dto = new PickTopBuildingsDTO(playerNickname,cardIndex); //prepares the dto if needed

        model.getChangeHandler().registerState();
        turnManager.getPhase().pickCardFromTop(turnManager, activePlayer, card, model.getBoard());

        //if the operation was not successful the notice won't be sent
        listeners.forEach(l -> l.update(dto));
        model.getChangeHandler().getChanges().forEach(c ->
                listeners.forEach(l -> l.update(c)));
    }

    /**
     * Handles the request of skipping the pick phase.
     *
     * @param playerNickname the player performing the action.
     * @throws IllegalGameActionException if the action is not allowed in the current phase or if the player is trying
     *                                    to perform an action that is not his turn.
     */
    public void handlePickSkip(String playerNickname) throws IllegalGameActionException {
        TurnManager turnManager = model.getTurnManager();
        Player activePlayer = turnManager.getActivePlayer();

        if (!activePlayer.getNickname().equals(playerNickname)) {
            throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
        }

        model.getChangeHandler().registerState();
        turnManager.getPhase().skipPick(turnManager, activePlayer);

        model.getChangeHandler().getChanges().forEach(c ->
                listeners.forEach(l -> l.update(c)));
    }

    /**
     * Sends to the clients the info for the initial game state as property change event.
     * @throws IllegalStateException if the game has already ended.
     */
    public void sendGameStartInfo() throws IllegalStateException{
        if(isGameFinished()) throw new IllegalStateException();
        listeners.forEach(l -> l.update(model.getChangeHandler().getStartingStateAsDTO()));
    }

    public boolean isGameFinished() {
        return model.getBoard().isEndGame();
    }

    public void addListener(ModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ModelListener listener) {
        listeners.remove(listener);
    }

}