package it.polimi.gc06.mesos.Model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class GameModel {
    private ArrayList<TileSlot> offerTrack;
    private TurnOrderTile turnOrderTile;

    private Board board;

    private EnumMap<Era, List<BuildingCard>> buildingCardsDecks;

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

    public EnumMap<Era, List<BuildingCard>> getBuildingCardsDecks() {
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
