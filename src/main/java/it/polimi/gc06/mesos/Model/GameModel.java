package it.polimi.gc06.mesos.Model;

import java.util.ArrayList;

public class GameModel {
    private ArrayList<TileSlot> offerTrack;
    private TurnOrderTile turnOrderTile;

    private Board board;

    // current solution but may be switched with the bottom version
    private ArrayList<ArrayList<BuildingCard>> buildingCardsDecks;

//    private ArrayList<BuildingCard> buildingCardsEraIDeck;
//    private ArrayList<BuildingCard> buildingCardsEraIIDeck;
//    private ArrayList<BuildingCard> buildingCardsEraIIIDeck;

    private ArrayList<TribeCard> tribeCardsDeck;

    private EventCard[] finalEventCards;

    private ArrayList<Player> players;

    private TurnManager turnManager;


    public GameModel() {
    }

    protected boolean startGame() {return true;};
    protected boolean endGame() {return true;};

    protected ArrayList<TileSlot> getOfferTrack() {
        return offerTrack;
    }

    protected TurnOrderTile getTurnOrderTile() {
        return turnOrderTile;
    }

//    public ArrayList<BuildingCard> getBuildingCardsEraIDeck() {
//        return buildingCardsEraIDeck;
//    }
//
//    public ArrayList<BuildingCard> getBuildingCardsEraIIDeck() {
//        return buildingCardsEraIIDeck;
//    }
//
//    public ArrayList<BuildingCard> getBuildingCardsEraIIIDeck() {
//        return buildingCardsEraIIIDeck;
//    }

    // current solution but may be switched with the version above
    public ArrayList<ArrayList<BuildingCard>> getBuildingCardsDecks() {
        return buildingCardsDecks;
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

    public TurnManager getTurnManager() {
        return turnManager;
    }

}
