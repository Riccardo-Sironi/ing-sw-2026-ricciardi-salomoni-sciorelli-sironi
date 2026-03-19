package it.polimi.gc06.mesos.controller;

import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;

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

        model.getTurnManager().getPhase().action(model.getTurnManager()); // ???? how do we do this
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