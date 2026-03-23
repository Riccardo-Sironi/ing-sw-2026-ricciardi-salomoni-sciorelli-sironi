package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.gameExceptions.GameObjectNotFoundException;
import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameTurnManager.DrawObserver;
import it.polimi.gc06.mesos.model.gameTurnManager.Phase;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;

import java.util.*;

public class GameModel implements GameInfo {
    private final Board board;

    private final EnumMap<Era, ArrayList<BuildingCard>> buildingCardsDecks;

    private final EnumMap<Era, ArrayList<TribeCard>> tribeCardsDeck;

    private final EventCard[] finalEventCards;

    private final ArrayList<Player> players;

    private final TurnManager turnManager;

    public GameModel() {
        // TODO NOTE : this is just the skeleton, but wondering if we should have a method called by the controller
        // that fills the lists and maps with the correct number of objects based on player quantity.

        // TODO : first thing is to initialize the players (and assign them the starting food tokens according to the player order ?).
        this.players = new ArrayList<>();

        // TODO : init the decks with the cards from the json files.
        this.buildingCardsDecks = new EnumMap<>(Era.class);
        this.tribeCardsDeck = new EnumMap<>(Era.class);
        this.finalEventCards = new EventCard[2];

        // TODO : after the decks initialization we have to create the board and initialize it.
        this.board = new Board();

        // TODO : after the board initialization we have to create the turn manager and initialize it with the first phase (PlacingTotemPhase) and the list of players.
        this.turnManager = new TurnManager(players, board.getTurnOrderTile(), 0, board.getOfferTrack(), null); // the parameters here are not clear yet (except for the list of players)
    }

    /**
     * method that starts the game session
     *
     * @return true if the game started successfully.
     * otherwise return false.
     */
    public boolean startGame() {
        // TODO : we should fill all the decks here and other initialization stuff (like board init)

        if ((players.size() < 2 || players.size() > 5) /*|| se la parita è già iniziata*/) {
            return false;
        }

        setupPlayersOrderAndFood();

        setupDecksAndBoard();

        //TODO: passare al turn manager la lista dei giocatori shuffolata.
//        this.turnManager = new TurnManager(
//               da implementare
//        )


        return true;
    }

    /**
     * this method shuffles the list of players to determine the player order and assigns the starting
     * food tokens based on their position in the order.
     *
     */
    private void setupPlayersOrderAndFood() {

        Collections.shuffle(this.players);

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if (i == 0) {
                p.addFoodTokens(2);
            } else if (i == 1 || i == 2) {
                p.addFoodTokens(3);
            } else if (i == 3 || i == 4) {
                p.addFoodTokens(4);
            }
        }

    }

    /**
     * this method is used to create the decks
     * TODO: logic of setupDecksAndBoard() method.
     */
    private void setupDecksAndBoard() {

    }

    /**
     * method that ends the game session and triggers final scoring.
     *
     * @return true if the game ended successfully.
     * TODO: false otherwise.
     */
    public boolean endGame() {
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
     * this method is used to add an observer to the board
     * to be notified during the card drawing process.
     * It checks if the observer is already in the list
     *
     * @param observer the observer to add.
     */
    @Override
    public void addObserver(DrawObserver observer) {
        board.addObserver(observer);
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
