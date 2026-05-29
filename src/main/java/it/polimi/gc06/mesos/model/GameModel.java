package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.dtos.LobbyInitializedDTO;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileEffect;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameTurnManager.DrawObserver;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;
import it.polimi.gc06.mesos.network.leaderboard.Leaderboard;
import it.polimi.gc06.mesos.network.leaderboard.Score;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

public class GameModel implements GameInfo {

    private final Board board;
    private final EnumMap<Era, ArrayList<BuildingCard>> buildingCardsDecks;
    private final EnumMap<Era, ArrayList<TribeCard>> tribeCardsDeck;
    private final EventCard[] finalEventCards;
    private final ArrayList<Player> players;
    private final TurnManager turnManager;
    private final DTONotifier notifier;
    private Timestamp endTimestamp;

    public GameModel(Board board, EnumMap<Era, ArrayList<BuildingCard>> buildingCardsDecks,
                     EnumMap<Era, ArrayList<TribeCard>> tribeCardsDeck, EventCard[] finalEventCards,
                     ArrayList<Player> players, TurnManager turnManager, DTONotifier notifier) {
        this.board = board;
        this.buildingCardsDecks = buildingCardsDecks;
        this.tribeCardsDeck = tribeCardsDeck;
        this.finalEventCards = finalEventCards;
        this.players = players;
        this.turnManager = turnManager;
        this.notifier = notifier;
        this.endTimestamp = null;
    }

    /**
     * This method initializes the game session by setting up the board, randomizing player order, and distributing
     * initial food tokens based on player count. It also checks that the number of players is within the allowed
     * range (2 to 5) before starting the game.
     */
    public void startGame() {
        if ((players.size() < 2 || players.size() > 5)) {
            throw new IllegalStateException("Player count must be between 2 and 5.");
        }

        for (Era e : Era.values()) {
            Collections.shuffle(tribeCardsDeck.get(e));
            Collections.shuffle(buildingCardsDecks.get(e));
        }

        try {
            board.initBoard(this);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Failed to initialize the board: " + e.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex.getMessage());
        }

        buildingCardsDecks.clear();

        ArrayList<Player> playersList = (ArrayList<Player>) turnManager.getPlayersOrder();

        Collections.shuffle(playersList); //randomize player order

        //setups the initial food
        for (int i = 0; i < playersList.size(); i++) {
            Player p = playersList.get(i);
            board.getTurnOrderTile().slots().get(i).setPlayer(p);
            if (i == 0) {
                p.addFoodTokens(2);
            } else if (i == 1 || i == 2) {
                p.addFoodTokens(3);
            } else if (i == 3 || i == 4) {
                p.addFoodTokens(4);
            }
        }

        //prepares notification for all player
        for(Player p : players){
            notifier.notifyChangeToPlayer(p.getNickname(),getStartingStateAsDTO(p.getNickname()));
        }
    }

    public SmallModelEditor getStartingStateAsDTO(String nickname) {

        Map<String, Integer> foodMap = new HashMap<>();
        Map<String, Color> colorMap = new HashMap<>();

        int topDrawNum = 0;
        int bottomDrawNum = 0;

        for (Player p : getPlayers()) {
            foodMap.put(p.getNickname(), p.getFoodTokens());
            colorMap.put(p.getNickname(), p.getPlayerColor());

            if (p.getNickname().equals(nickname)) {
                topDrawNum = p.getTopDrawNum();
                bottomDrawNum = p.getBottomDrawNum();
            }
        }

        boolean isActive = turnManager.getActivePlayer().getNickname().equals(nickname);

        ArrayList<TileEffect> effects = new ArrayList<>(
                board.getOfferTrack().stream().map(TileSlot::getTileEffect).toList()
        );

        int currentDeckSize = 0;
        if (getTribeCardsDeck() != null) {
            currentDeckSize = getTribeCardsDeck().get(Era.ERA_I).size()
                    + getTribeCardsDeck().get(Era.ERA_II).size()
                    + getTribeCardsDeck().get(Era.ERA_III).size();
        }

        return new LobbyInitializedDTO(
                nickname,
                new ArrayList<>(turnManager.getPlayersOrder().stream().map(Player::getNickname).toList()),
                colorMap,
                foodMap,
                new ArrayList<>(board.getTopRow()),
                new ArrayList<>(board.getTopBuildings()),
                new ArrayList<>(board.getBottomRow()),
                new ArrayList<>(board.getBottomBuildings()),
                isActive,
                effects,
                topDrawNum,
                bottomDrawNum,
                currentDeckSize
        );
    }

    /**
     * This method calculates the final prestige tokens for each player at the end of the game based on various factors
     * such as builders prestige, inventors counter, artists counter, and the prestige gain from building cards.
     * After calculating the prestige tokens, it sorts the players in descending order based on their prestige tokens
     * and food tokens to determine the final ranking of the players.
     */
    public void endGame() {

        players.forEach(p -> p.addPrestigeTokens(p.getBuildersPrestige()));
        players.forEach(p -> p.addPrestigeTokens(p.getInventorsCounter() * p.getNumOfIcon()));
        players.forEach(p -> p.addPrestigeTokens(p.getArtistsCounter() / 2 * 10));
        players.forEach(p -> p.addPrestigeTokens(
                p.getBuildingCards().stream().mapToInt(b -> b.getPrestigeGain(p)).sum()
        ));

        players.sort(Comparator.comparing(Player::getPrestigeTokens)
                .thenComparing(Player::getFoodTokens)
                .reversed());
        endTimestamp = Timestamp.from(Instant.now());
    }

    /**
     * This method retrieves the maximum number of shaman stars currently held by any player.
     *
     * @return the highest number of shaman stars among all players. If there are no players, returns 0
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

    @Override
    public int getNumPlayerMaxStars() {
        int maxStars = this.getMaxStars();
        return (int) players.stream()
                .filter(p -> p.getShamanStars() == maxStars)
                .count();
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

    /**
     * Leaderboard getter.
     *
     * @return the leaderboard of the match, with timestamp the moment of the call
     * @throws IllegalStateException if the game is not finished yet.
     */
    public Leaderboard getLeaderboard() throws IllegalStateException{
        if (!isFinished()) throw new IllegalStateException("Game is not finished yet");

        Leaderboard leaderboard = new Leaderboard();
        leaderboard.setTimestamp(endTimestamp);
        for (Player p : players) {
            leaderboard.addScore(new Score(p.getNickname(), p.getPrestigeTokens(), p.getFoodTokens()));
        }
        return leaderboard;
    }

    /**
     * Returns whether the game has ended.
     *
     * @return true if it has finished.
     */
    public boolean isFinished(){
        return endTimestamp != null;
    }
}
