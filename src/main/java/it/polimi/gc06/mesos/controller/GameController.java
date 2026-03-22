package it.polimi.gc06.mesos.controller;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameTurnManager.OfferResolutionPhase;

public class GameController {
    private GameModel model;

    public GameController(GameModel model) {
        this.model = model;
    }

    public GameModel getModel() {
        return model;
    }

    public void handleTotemOfferTilePlacement(String playerNickname, TileSlot tile) {
        if (!model.getTurnManager().getActivePlayer().getNickname().equals(playerNickname)) {
            // TODO : handle this
        }

        if (tile.getPlayer() != null) {
            // TODO : handle this
        }

        try {
            model.getTurnManager().getPhase().placeTotem(model.getTurnManager(), model.getTurnManager().getActivePlayer(), tile);
        } catch (IllegalPhaseActionException e) {
            System.out.println(e.getMessage()); // TODO : communicate the error to the view
        }
    }

    public void handleCardPickTopRow(String playerNickname, TribeCard card) {
        if (!model.getTurnManager().getActivePlayer().getNickname().equals(playerNickname)) {
            // TODO : handle this
        }
    }

    public void handleCardPickBottomRow(String playerNickname, TribeCard card) {
        if (!model.getTurnManager().getActivePlayer().getNickname().equals(playerNickname)) {
            // TODO : handle this
        }
    }

    public void handleBuildingBuyingTopRow(String playerNickname, BuildingCard card) {
        if (!model.getTurnManager().getActivePlayer().getNickname().equals(playerNickname)) {
            // TODO : handle this
        }
    }

    public void handleBuildingBuyingBottomRow(String playerNickname, BuildingCard card) {
        if (!model.getTurnManager().getActivePlayer().getNickname().equals(playerNickname)) {
            // TODO : handle this
        }
    }
}