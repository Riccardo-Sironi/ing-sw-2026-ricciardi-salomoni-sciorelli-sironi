package it.polimi.gc06.mesos.controller;

import it.polimi.gc06.mesos.dtos.*;
import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.Color;
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

public class GameController {

    private final GameModel model;
    private final ArrayList<ModelListener> listeners;
    private final Map<String, ModelListener> listenerMap;

    public GameController(GameModel model) {
        this.model = model;
        this.listeners = new ArrayList<>();
        this.listenerMap = new HashMap<>();
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

        model.getChangeHandler().registerState();
        turnManager.getPhase().placeTotem(turnManager, activePlayer, tile, model.getBoard());

        TotemOfferMoveDTO dto = new TotemOfferMoveDTO(playerNickname, tileIndex);
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
        PickBottomRowDTO dto = new PickBottomRowDTO(playerNickname, cardIndex); //prepares the dto if needed

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
        PickTopRowDTO dto = new PickTopRowDTO(playerNickname, cardIndex); //prepares the dto if needed

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
        PickBottomBuildingsDTO dto = new PickBottomBuildingsDTO(playerNickname, cardIndex); //prepares the dto if needed

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
        PickTopBuildingsDTO dto = new PickTopBuildingsDTO(playerNickname, cardIndex); //prepares the dto if needed

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
     * to perform an action that is not his turn.
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
     * Sends to the clients the info for the initial game state as a SmallModelEditor.
     *
     * @throws IllegalStateException if the game has already ended.
     */
    public void sendGameStartInfo() throws IllegalStateException {
        if (isGameFinished()) throw new IllegalStateException();
        listenerMap.forEach((s, l) -> l.update(model.getChangeHandler().getStartingStateAsDTO(s)));
    }

    /**
     * This method is used to handle the request of totem color choice from the player,
     * it updates the model and notifies all clients about the change.
     *
     * @param playerNickname the nickname of the player who is choosing the totem color.
     * @param color          the color chosen by the player for their totem.
     */

    public void handleChooseTotemColor(String playerNickname, Color color) {
        Player p = model.getPlayers().stream()
                .filter(player -> player.getNickname().equals(playerNickname))
                .findFirst()
                .orElse(null);

        if (p != null) {
            p.setPlayerColor(color);
        }

        ChooseTotemColorDTO dto = new ChooseTotemColorDTO(playerNickname, color);
        listeners.forEach(l -> l.update(dto));

        boolean areAllPlayersReady = model.getPlayers().stream().allMatch(player -> player.getPlayerColor() != null);

        // if all the players have selected di color we send the DTO to start the game
        if (areAllPlayersReady) {
            MesosStartedDTO dto2 = new MesosStartedDTO();
            listeners.forEach(l -> l.update(dto2));
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

    /**
     * Subscribes a listener to the game controller updates.
     *
     * @param listener the ModelListener to be added.
     * @param nickname the nickname associated with the listener.
     */
    public void addListener(ModelListener listener, String nickname) {
        listeners.add(listener);
        listenerMap.put(nickname, listener);
    }

    /**
     * Unsubscribes a listener from the game controller updates.
     *
     * @param listener the ModelListener to be removed.
     * @param nickname the nickname associated with the listener.
     */
    public void removeListener(ModelListener listener, String nickname) {
        listeners.remove(listener);
        listenerMap.remove(nickname);
    }

}