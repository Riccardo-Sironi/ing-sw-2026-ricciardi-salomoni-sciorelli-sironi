package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;

import java.util.ArrayList;
import java.util.EnumMap;

public class GameModel implements GameInfo {
    private Board board;

    private EnumMap<Era, ArrayList<BuildingCard>> buildingCardsDecks;

    //private ArrayList<TribeCard> tribeCardsDeck;
    private EnumMap<Era, ArrayList<TribeCard>> tribeCardsDeck;

    private EventCard[] finalEventCards;

    private ArrayList<Player> players;

    private TurnManager turnManager;


    public GameModel() {
    }

    protected boolean startGame() {
        return true;
    }

    protected boolean endGame() {
        return true;
    }

    @Override
    public int getMaxStars() {
        // Functional approach to get the maximum number of shaman stars among all players. If there are no players, returns 0.
        return players.stream()
                .mapToInt(Player::getShamanStars)
                .max()
                .orElse(0);
    }

    @Override
    public int getMinStars() {
        // Functional approach to get the minimum number of shaman stars among all players. If there are no players, returns 0.
        return players.stream()
                .mapToInt(Player::getShamanStars)
                .min()
                .orElse(0);
    }

    public EnumMap<Era, ArrayList<BuildingCard>> getBuildingCardsDecks() {
        return buildingCardsDecks;
    }

    public EnumMap<Era, ArrayList<TribeCard>> getTribeCardsDeck() {
        return tribeCardsDeck;
    }

    public EventCard[] getFinalEventCards() {
        return finalEventCards;
    }

    protected Board getBoard() {
        return board;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public TurnManager getTurnManager() {
        return turnManager;
    }
}
