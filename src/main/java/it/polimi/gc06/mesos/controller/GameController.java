package it.polimi.gc06.mesos.controller;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;

public class GameController {
    private final GameModel model;

    public GameController(GameModel model) {
        this.model = model;
    }

    public GameModel getModel() {
        return model;
    }

    public void handleGameInitialization() {
        // TODO : understand what we should do here
        model.startGame();
    }

    public void handleTotemOfferTilePlacement(String playerNickname, TileSlot tile) {
        TurnManager turnManager = model.getTurnManager();

        try {
            Player activePlayer = turnManager.getActivePlayer();

            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("It is not " + playerNickname + " turn !");
            }

            if (tile.getPlayer() != null) {
                throw new IllegalPhaseActionException("The tile is already occupied !");
            }

            turnManager.getPhase().placeTotem(turnManager, activePlayer, tile);
        } catch (IllegalPhaseActionException e) {
            System.err.println("Error for " + playerNickname + ": " + e.getMessage()); // TODO : communicate the error to the view
        }
    }

    public void handleCardPickTopRow(String playerNickname, CharacterCard card) {
        TurnManager turnManager = model.getTurnManager();

        try {
            Player activePlayer = turnManager.getActivePlayer();
            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
            }
            turnManager.getPhase().pickCardFromTop(turnManager, activePlayer, card, model.getBoard());

        } catch (IllegalPhaseActionException | IllegalArgumentException e) {
            System.err.println("Error for " + playerNickname + ": " + e.getMessage());
            // TODO: Inviare un pacchetto di Errore al Client
        }
    }

    public void handleCardPickBottomRow(String playerNickname, CharacterCard card) {
        TurnManager turnManager = model.getTurnManager();
        try {
            Player activePlayer = turnManager.getActivePlayer();
            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
            }
            turnManager.getPhase().pickCardFromBottom(turnManager, activePlayer, card, model.getBoard());

        } catch (IllegalPhaseActionException | IllegalArgumentException e) {
            System.err.println("Error for " + playerNickname + ": " + e.getMessage());
            // TODO: Inviare un pacchetto di Errore al Client
        }
    }

    public void handleCardPickBottomRow(String playerNickname, BuildingCard card) {
        TurnManager turnManager = model.getTurnManager();

        try {
            Player activePlayer = turnManager.getActivePlayer();

            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
            }

            turnManager.getPhase().pickCardFromBottom(turnManager, activePlayer, card, model.getBoard());
        } catch (IllegalPhaseActionException | IllegalArgumentException e) {
            System.err.println("Error for " + playerNickname + ": " + e.getMessage()); // TODO : communicate the error to the view
        }
    }

    public void handleCardPickTopRow(String playerNickname, BuildingCard card) {
        TurnManager turnManager = model.getTurnManager();

        try {
            Player activePlayer = turnManager.getActivePlayer();

            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
            }

            turnManager.getPhase().pickCardFromTop(turnManager, activePlayer, card, model.getBoard());
        } catch (IllegalPhaseActionException | IllegalArgumentException e) {
            System.err.println("Error for " + playerNickname + ": " + e.getMessage()); // TODO : communicate the error to the view
        }
    }
}