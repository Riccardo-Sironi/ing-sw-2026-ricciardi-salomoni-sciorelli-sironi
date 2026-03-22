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
        if (!model.getTurnManager().getActivePlayer().getNickname().equals(playerNickname)) {
            // TODO : handle this
        }

        if (tile.getPlayer() != null) {
            // TODO : handle this
        }

        TurnManager turnManager = model.getTurnManager();

        try {
            turnManager.getPhase().placeTotem(turnManager, turnManager.getActivePlayer(), tile);
        } catch (IllegalPhaseActionException e) {
            System.out.println(e.getMessage()); // TODO : communicate the error to the view
        }
    }

    public void handleCardPickTopRow(String playerNickname, CharacterCard card) {
        TurnManager turnManager = model.getTurnManager();

        try {
            Player activePlayer = turnManager.getActivePlayer();
            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("Non è il tuo turno!");
            }
            turnManager.getPhase().pickCardFromTop(turnManager, activePlayer, card, model.getBoard());

        } catch (IllegalPhaseActionException | IllegalArgumentException e) {
            System.err.println("Errore per " + playerNickname + ": " + e.getMessage());
            // TODO: Inviare un pacchetto di Errore al Client
        }
    }

    public void handleCardPickBottomRow(String playerNickname, BuildingCard card) {
        if (!model.getTurnManager().getActivePlayer().getNickname().equals(playerNickname)) {
            // TODO : handle this
        }

        try {
            // TODO : we need the phase method to pick a building card
        } catch (IllegalPhaseActionException e) {
            System.out.println(e.getMessage()); // TODO : communicate the error to the view
        }
    }

    public void handleCardPickTopRow(String playerNickname, BuildingCard card) {
        if (!model.getTurnManager().getActivePlayer().getNickname().equals(playerNickname)) {
            // TODO : handle this
        }

        try {
            // TODO : we need the phase method to pick a building card
        } catch (IllegalPhaseActionException e) {
            System.out.println(e.getMessage()); // TODO : communicate the error to the view
        }
    }

    public void handleCardPickBottomRow(String playerNickname, CharacterCard card) {
        TurnManager turnManager = model.getTurnManager();
        try {
            Player activePlayer = turnManager.getActivePlayer();
            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("Non è il tuo turno!");
            }
            turnManager.getPhase().pickCardFromBottom(turnManager, activePlayer, card, model.getBoard());

        } catch (IllegalPhaseActionException | IllegalArgumentException e) {
            System.err.println("Errore per " + playerNickname + ": " + e.getMessage());
            // TODO: Inviare un pacchetto di Errore al Client
        }
    }
}