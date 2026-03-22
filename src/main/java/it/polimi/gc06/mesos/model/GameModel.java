package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.gameExceptions.GameObjectNotFoundException;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameTurnManager.Phase;
import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;

import java.util.*;

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

    /**
     * method that starts the game session
     *
     * @return true if the game started successfully.
     * TODO: false otherwise.
     */
    protected boolean startGame() {
        if (players.size() < 2 || players.size() > 5) {
            return false;
        }

        Collections.shuffle(this.players);

        //TODO: passare al turn manager la lista dei giocatori shuffolata.

        for(int i = 0; i < players.size(); i++){
            Player p = players.get(i);
            if (i == 0){ p.addFoodTokens(2); }
            else if (i == 1 || i == 2){ p.addFoodTokens(3); }
            else if (i == 3 || i == 4) { p.addFoodTokens(4); }
        }

        return true;
    }

    /**
     * method that ends the game session and triggers final scoring.
     *
     * @return true if the game ended successfully.
     * TODO: false otherwise.
     */
    protected boolean endGame() {
        //TODO: method to count final prestige tokens (considering builders, inventors...)

        players.sort(Comparator.comparing(Player::getPrestigeTokens)
                .thenComparing(Player::getFoodTokens)
                .reversed());
        return true;
    }

    /**
     * this method retrieves the maximum number of shaman stars currently held by any player.
     *
     * @return the highest number of shaman stars among all players.
     * if there are no players, returns 0
     */
    @Override
    public int getMaxStars() {
        // Functional approach to get the maximum number of shaman stars among all players. If there are no players, returns 0.
        return players.stream()
                .mapToInt(Player::getShamanStars)
                .max()
                .orElse(0);
    }

    /**
     * this method retrieves the minimum number of shaman stars currently held by any player.
     *
     * @return the lowest number of shaman stars among all players.
     * if there are no players, returns 0
     */
    @Override
    public int getMinStars() {
        // Functional approach to get the minimum number of shaman stars among all players. If there are no players, returns 0.
        return players.stream()
                .mapToInt(Player::getShamanStars)
                .min()
                .orElse(0);
    }

    /**
     * phase getter.
     *
     * @return the current game phase
     */
    @Override
    public Phase getCurrentPhase() throws GameObjectNotFoundException {
        Phase currentPhase = turnManager.getPhase();
        if (currentPhase == null) throw new GameObjectNotFoundException("Phase not found.");
        return currentPhase;
    }

    /**
     * this method retrieves the deck containing all building cards, categorized by Era.
     *
     * @return an EnumMap mapping each Era to its corresponding list of building cards.
     */
    public EnumMap<Era, ArrayList<BuildingCard>> getBuildingCardsDecks() {
        return buildingCardsDecks;
    }

    /**
     * this method retrieves the deck containing all tribe cards, categorized by Era.
     *
     * @return an EnumMap mapping each Era to its corresponding list of tribe cards.
     */
    public EnumMap<Era, ArrayList<TribeCard>> getTribeCardsDeck() {
        return tribeCardsDeck;
    }

    /**
     * this method retrieves the final event cards used at the end of the game.
     *
     * @return an array containing the 2 final event cards.
     */
    public EventCard[] getFinalEventCards() {
        return finalEventCards;
    }

    /**
     * this method retrieves the physical game board.
     *
     * @return the board managing the cards currently in play.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * this method retrieves the list of players currently participating in the game.
     *
     * @return an ArrayList containing all players in the game.
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * this method retrieves the turn manager responsible for orchestrating the game phases.
     *
     * @return the current TurnManager.
     */
    public TurnManager getTurnManager() {
        return turnManager;
    }
}
