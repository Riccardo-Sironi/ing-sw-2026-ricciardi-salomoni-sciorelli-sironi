package it.polimi.gc06.mesos.controller;

import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;

public class GameController {
    private GameModel model;

    public GameController(GameModel model) {
        this.model = model;
    }

    public GameModel getModel() {
        return model;
    }

    public void handleTotemOfferTilePlacement(String playerNickname, TileSlot slot) {
    }

    public void handleCardPick(String playerNickname, TribeCard card) {
    }

    public void handleBuildingBuying(String playerNickname, BuildingCard building) {
    }
}
