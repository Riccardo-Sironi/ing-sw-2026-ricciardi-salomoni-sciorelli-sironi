package it.polimi.gc06.mesos.Model;

import java.util.ArrayList;

public class GameModel {
    private ArrayList<TileSlot> offerTrack;
    private TurnOrderTile turnOrderTile;

    private Board board;

    private int foodGeneralSupply;
    private int prestigeGeneralSupply;

    private ArrayList<BuildingCard> buildingCardsDeck;
    private ArrayList<TribeCard> tribeCardsDeck;

    private EventCard[] finalEventCards;

    private ArrayList<Player> players;

    private TurnManager turnManager;


    public GameModel() {
    }

    protected boolean startGame() {};
    protected boolean endGame() {};

    protected ArrayList<TileSlot> getOfferTrack() {
        return offerTrack;
    }

    protected TurnOrderTile getTurnOrderTile() {
        return turnOrderTile;
    }

    protected ArrayList<BuildingCard> getBuildingCardsDeck() {
        return buildingCardsDeck;
    }

    protected ArrayList<TribeCard> getTribeCardsDeck() {
        return tribeCardsDeck;
    }

    protected EventCard[] getFinalEventCards() {
        return finalEventCards;
    }

    protected Board getBoard() {
        return board;
    }

    protected ArrayList<Player> getPlayers() {
        return players;
    }

    protected int getFoodGeneralSupply() {
        return foodGeneralSupply;
    }

    protected int getPrestigeGeneralSupply() {
        return prestigeGeneralSupply;
    }

    public TurnManager getTurnManager() {
        return turnManager;
    }

}
