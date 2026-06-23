package it.polimi.gc06.mesos.controller;

import it.polimi.gc06.mesos.dtos.ChooseTotemColorDTO;
import it.polimi.gc06.mesos.dtos.ErrorDTO;
import it.polimi.gc06.mesos.dtos.MesosStartedDTO;
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

import java.util.List;

/**
 * The main controller for the Mesos game.
 * It handles incoming player requests, validates if it is the player's turn,
 * and delegates the execution of the actions to the underlying game model and turn manager.
 */
public class GameController {

    private final GameModel model;
    private final DTONotifier notifier;

    /**
     * Constructs a new GameController.
     *
     * @param model The game model to manage.
     * @param notifier The notifier used to broadcast state changes to clients.
     */
    public GameController(GameModel model, DTONotifier notifier) {
        this.model = model;
        this.notifier = notifier;
    }

    /**
     * Retrieves the main game model associated with this controller.
     *
     * @return The GameModel instance.
     */
    public GameModel getModel() {
        return model;
    }

    /**
     * Handles the request of tile placement.
     *
     * @param playerNickname The player performing the action.
     * @param tileIndex The index of the tile where the player wants to be placed.
     * @throws IllegalPhaseActionException If the action is not allowed in the current phase or if the player is trying to perform an action that is not his turn.
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
     * @param playerNickname The player performing the action.
     * @param cardIndex The index of the card that the player wants to pick.
     * @throws IllegalPhaseActionException If the player is trying to perform an action that is not his turn.
     * @throws IllegalGameActionException If the action is not allowed in the current phase.
     * @throws IndexOutOfBoundsException If the card index is out of bounds.
     */
    public void handleCardPickBottomRow(String playerNickname, int cardIndex) throws IllegalPhaseActionException, IndexOutOfBoundsException {
        TurnManager turnManager = model.getTurnManager();
        Player activePlayer = turnManager.getActivePlayer();
        if (!activePlayer.getNickname().equals(playerNickname)) {
            throw new IllegalPhaseActionException("It's not " + playerNickname + "'s turn !");
        }

        TribeCard card = model.getBoard().getBottomCardFromIndex(cardIndex);

        CardBottomRowControllerVisitor cardPickerVisitor = new CardBottomRowControllerVisitor(turnManager, activePlayer, model);
        card.accept(cardPickerVisitor);
    }

    /**
     * Handles the request of card picking from top row.
     *
     * @param playerNickname The player performing the action.
     * @param cardIndex The index of the card that the player wants to pick.
     * @throws IllegalPhaseActionException If the player is trying to perform an action that is not his turn.
     * @throws IllegalGameActionException If the action is not allowed in the current phase or if the player is trying to perform an action that is not his turn.
     * @throws IndexOutOfBoundsException If the card index is out of bounds.
     */
    public void handleCardPickTopRow(String playerNickname, int cardIndex) throws IllegalPhaseActionException, IndexOutOfBoundsException {
        TurnManager turnManager = model.getTurnManager();

        Player activePlayer = turnManager.getActivePlayer();
        if (!activePlayer.getNickname().equals(playerNickname)) {
            throw new IllegalPhaseActionException("It's not " + playerNickname + "'s turn !");
        }

        TribeCard card = model.getBoard().getTopCardFromIndex(cardIndex);

        CardTopRowControllerVisitor cardPickerVisitor = new CardTopRowControllerVisitor(turnManager, activePlayer, model);
        card.accept(cardPickerVisitor);
    }

    /**
     * Handles the request of building picking from bottom row.
     *
     * @param playerNickname The player performing the action.
     * @param cardIndex The index of the building that the player wants to pick.
     * @throws IllegalPhaseActionException If the player is trying to perform an action that is not his turn.
     * @throws IllegalGameActionException If the action is not allowed in the current phase or if the player is trying to perform an action that is not his turn.
     * @throws IndexOutOfBoundsException If the card index is out of bounds.
     */
    public void handleBuildingPickBottomRow(String playerNickname, int cardIndex) throws IllegalPhaseActionException, IndexOutOfBoundsException {
        TurnManager turnManager = model.getTurnManager();


        Player activePlayer = turnManager.getActivePlayer();

        if (!activePlayer.getNickname().equals(playerNickname)) {
            throw new IllegalPhaseActionException("It's not " + playerNickname + "'s turn !");
        }

        BuildingCard card = model.getBoard().getBottomBuildingFromIndex(cardIndex);
        turnManager.getPhase().pickCardFromBottom(turnManager, activePlayer, card, model.getBoard());
    }

    /**
     * Handles the request of building picking from top row.
     *
     * @param playerNickname The player performing the action.
     * @param cardIndex The index of the building that the player wants to pick.
     * @throws IllegalPhaseActionException If the player is trying to perform an action that is not his turn.
     * @throws IllegalGameActionException If the action is not allowed in the current phase or if the player is trying to perform an action that is not his turn.
     * @throws IndexOutOfBoundsException If the card index is out of bounds.
     */
    public void handleBuildingPickTopRow(String playerNickname, int cardIndex) throws IllegalPhaseActionException, IndexOutOfBoundsException {
        TurnManager turnManager = model.getTurnManager();
        Player activePlayer = turnManager.getActivePlayer();

        if (!activePlayer.getNickname().equals(playerNickname)) {
            throw new IllegalPhaseActionException("It's not " + playerNickname + "'s turn !");
        }

        BuildingCard card = model.getBoard().getTopBuildingFromIndex(cardIndex);
        turnManager.getPhase().pickCardFromTop(turnManager, activePlayer, card, model.getBoard());
    }

    /**
     * Handles the request of skipping the pick phase.
     *
     * @param playerNickname The player performing the action.
     * @throws IllegalGameActionException If the action is not allowed in the current phase or if the player is trying
     * to perform an action that is not his turn.
     */
    public void handlePickSkip(String playerNickname) throws IllegalGameActionException {
        TurnManager turnManager = model.getTurnManager();
        Player activePlayer = turnManager.getActivePlayer();

        if (!activePlayer.getNickname().equals(playerNickname)) {
            throw new IllegalPhaseActionException("It's not " + playerNickname + "'s turn !");
        }
        turnManager.getPhase().skipPick(turnManager, activePlayer);
    }

    /**
     * This method is used to handle the request of totem color choice from the player,
     * it updates the model and notifies all clients about the change.
     *
     * @param playerNickname The nickname of the player who is choosing the totem color.
     * @param color The color chosen by the player for their totem.
     */
    public void handleChooseTotemColor(String playerNickname, Color color) {

        boolean alreadyChosen = model.getPlayers().stream().anyMatch(player -> player.getPlayerColor() == color);

        if (alreadyChosen) {
            notifier.notifyChangeToPlayer(playerNickname, new ErrorDTO("Color " + color + " has already been chosen by another player."));
            return;
        }

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
            model.saveSnapshot(); //also saves the state of the game
        }

    }

    /**
     * Checks whether the game has reached the end state.
     *
     * @return True if the game is finished, false otherwise.
     */
    public boolean isGameFinished() {
        return model.isFinished();
    }
}