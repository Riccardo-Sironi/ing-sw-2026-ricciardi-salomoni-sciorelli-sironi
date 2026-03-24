package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.BottomRowInitVisitor;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.cards.events.EventListVisitor;
import it.polimi.gc06.mesos.model.gameTurnManager.DrawObserver;
import it.polimi.gc06.mesos.model.gameTurnManager.DrawSubject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;

public class Board implements DrawSubject {
    private final ArrayList<TribeCard> topRow;
    private final ArrayList<TribeCard> bottomRow;

    private final ArrayList<BuildingCard> topBuildings;
    private final ArrayList<BuildingCard> bottomBuildings;

    private final EnumMap<Era, ArrayList<BuildingCard>> buildingsDecks;

    final private TurnOrderTile turnOrderTile;
    final private List<TileSlot> offerTrack;

    private Era currentEra;

    private boolean isEndGame;

    private final ArrayList<DrawObserver> observers;

    public Board() {
        this.turnOrderTile = new TurnOrderTile(new ArrayList<>());
        this.offerTrack = new ArrayList<>();

        topRow = new ArrayList<>();
        bottomRow = new ArrayList<>();

        topBuildings = new ArrayList<>();
        bottomBuildings = new ArrayList<>();

        observers = new ArrayList<>();

        buildingsDecks = new EnumMap<>(Era.class);
        for (Era era : Era.values()) {
            buildingsDecks.put(era, new ArrayList<>());
        }

        currentEra = Era.ERA_I;
        isEndGame = false;
    }


    /**
     * Returns the turn order tile of the game, which contains the players in the order of their turns.
     *
     * @return the turn order tile of the game
     */
    public TurnOrderTile getTurnOrderTile() {
        return turnOrderTile;
    }

    /**
     * Returns the offer track of the game, which slots has different effects on the players based on their position
     * in the track.
     *
     * @return the tile slots array that compose the offer track of the game
     */
    public List<TileSlot> getOfferTrack() {
        return offerTrack;
    }


    /**
     * Returns the player's slot in the offer track, if the player is in the offer track, null otherwise.
     *
     * @return the player's slot in the offer track
     */
    public TileSlot getOfferTrackPlayerSlot(Player player) {
        for (TileSlot slot : offerTrack) {
            if (slot.getPlayer().equals(player))
                return slot;
        }
        return null;
    }

    /**
     *
     * Returns the top row of tribe cards.
     *
     * @return the array list of tribe cards in the top row.
     */
    public ArrayList<TribeCard> getTopRow() {
        return topRow;
    }

    /**
     *
     * Returns the bottom row of tribe cards.
     *
     * @return the array list of tribe cards in the bottom row.
     */
    public ArrayList<TribeCard> getBottomRow() {
        return bottomRow;
    }

    /**
     *
     * Returns the top row of building cards.
     *
     * @return the array list of building cards in the top building space.
     */
    public ArrayList<BuildingCard> getTopBuildings() {
        return topBuildings;
    }

    /**
     *
     * Return the bottom row of building cards.
     *
     * @return the array list of building cards in the bottom building space.
     */
    public ArrayList<BuildingCard> getBottomBuildings() {
        return bottomBuildings;
    }

    /**
     *
     * Return the decks of building cards for each era.
     *
     * @return the enum map of building cards decks based on their Era.
     */
    public EnumMap<Era, ArrayList<BuildingCard>> getBuildingsDecks() {
        return buildingsDecks;
    }

    /**
     *
     * Return the current era of the game.
     *
     * @return the enum value of current era.
     */
    public Era getCurrentEra() {
        return currentEra;
    }

    public boolean isOfferTrackEmpty() {
        for (TileSlot slot : offerTrack) {
            if (slot.getPlayer() != null) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * Initialize the board with the cards from the model.
     *
     * @param model Game model
     * @throws IllegalArgumentException if the model is null, if the tribe cards deck is null or empty, if the building
     *                                  cards decks are null or empty, if the number of players is not between 2 and 5 or
     *                                  if the final event cards are null or not 2.
     * @throws IllegalStateException    if the building cards deck for any era is empty during building decks initialization.
     */
    public void initBoard(GameModel model) throws IllegalArgumentException, IllegalStateException {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        if (model.getTribeCardsDeck() == null || model.getTribeCardsDeck().isEmpty() || model.getTribeCardsDeck().values().stream().allMatch(ArrayList::isEmpty)) {
            throw new IllegalArgumentException("Tribe cards deck cannot be null or empty");
        }
        if (model.getBuildingCardsDecks() == null || model.getBuildingCardsDecks().isEmpty() || model.getBuildingCardsDecks().values().stream().allMatch(ArrayList::isEmpty)) {
            throw new IllegalArgumentException("Building cards decks cannot be null or empty");
        }
        if (model.getPlayers().size() < 2 || model.getPlayers().size() > 5) {
            throw new IllegalArgumentException("Number of players must be between 2 and 5");
        }
        if (model.getFinalEventCards() == null || model.getFinalEventCards().length != 2) {
            throw new IllegalArgumentException("Final event cards cannot be null and there must be 2 of them");
        }

        int nPlayers = model.getPlayers().size();

        // create the decks of buildings cards
        // the rules specify the number of the buildings on the top row based on the number of player and Era,
        // which are respectively the columns and the rows of the matrix.
        int[][] nBuildings = {{1, 2, 3}, {2, 2, 4}, {2, 3, 4}, {2, 3, 5}};


        for (Era era : Era.values()) {
            for (int j = 0; j < nBuildings[nPlayers - 2][era.ordinal()]; j++) {
                if (model.getBuildingCardsDecks().get(era) == null || model.getBuildingCardsDecks().get(era).isEmpty()) {
                    throw new IllegalStateException("Building cards deck for era " + era + " cannot be null or empty " +
                            "during building decks initialization");
                }
                buildingsDecks.get(era).addLast(model.getBuildingCardsDecks().get(era).removeLast());
            }
        }

        // TODO : turn order tile init : !!!! THIS SHOULD BE DONE WITH THE JSON THIS IS JUST A PROTOTYPE !!!
        switch (nPlayers) {
            case 2:
                turnOrderTile.addSlot(0, new TileSlot(new FoodTileEffect(1)));
                turnOrderTile.addSlot(1, new TileSlot(new RemoveFoodTileEffect()));
                break;
            case 3:
                turnOrderTile.addSlot(0, new TileSlot(new FoodTileEffect(2)));
                turnOrderTile.addSlot(1, new TileSlot(null));
                turnOrderTile.addSlot(2, new TileSlot(new RemoveFoodTileEffect()));
                break;
            case 4:
                turnOrderTile.addSlot(0, new TileSlot(new FoodTileEffect(2)));
                turnOrderTile.addSlot(1, new TileSlot(new FoodTileEffect(1)));
                turnOrderTile.addSlot(2, new TileSlot(null));
                turnOrderTile.addSlot(3, new TileSlot(new RemoveFoodTileEffect()));
                break;
            case 5:
                turnOrderTile.addSlot(0, new TileSlot(new FoodTileEffect(3)));
                turnOrderTile.addSlot(1, new TileSlot(new FoodTileEffect(1)));
                turnOrderTile.addSlot(2, new TileSlot(null));
                turnOrderTile.addSlot(3, new TileSlot(null));
                turnOrderTile.addSlot(4, new TileSlot(new RemoveFoodTileEffect()));
                break;
            default:
                throw new IllegalArgumentException("Number of players must be between 2 and 5");
        }

        // offer track init
        if (nPlayers == 5) offerTrack.addFirst(new TileSlot(new FoodTileEffect(3)));
        offerTrack.add(new TileSlot(new ChooseCardTileEffect(0, 1)));
        offerTrack.add(new TileSlot(new ChooseCardTileEffect(1, 0)));
        if (nPlayers > 2) offerTrack.add(new TileSlot(new ChooseCardTileEffect(0, 2)));
        offerTrack.add(new TileSlot(new ChooseCardTileEffect(1, 1)));
        offerTrack.add(new TileSlot(new ChooseCardTileEffect(2, 0)));
        if (nPlayers > 3) offerTrack.addLast(new TileSlot(new ChooseCardTileEffect(2, 1)));

        // first and only initialization of bottom row tribe cards
        populateBottomRow(model);

        // top row initialization (there could be some events cards already)
        populateTopRow(model);

        // populate the top building cards space with the cards from deck of the current era (ERA_I in this case)
        populateTopBuildings();
    }

    /**
     *
     * Populate the top row of tribe cards. It is used in the initialization board method and at the end of each round.
     * It handles the transition between eras and the end game condition.
     *
     * @param model Game model
     * @throws IllegalArgumentException if the model is null, if the tribe cards deck is null or empty, if the number of
     *                                  players is not between 2 and 5 or if the final event cards are null or not 2.
     * @throws IllegalStateException    if the tribe cards deck for the current era is empty during the loop or if the top
     *                                  row is full while we try to populate it.
     */
    public void populateTopRow(GameModel model) throws IllegalArgumentException, IllegalStateException {
        // we need to subtract the toprow.size() for initialization purposes
        // (in the first round it's forbidden to have events in the bottom row so we
        // move them from the bottom to the top)

        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        if (model.getTribeCardsDeck() == null || model.getTribeCardsDeck().get(currentEra) == null) {
            throw new IllegalArgumentException("Tribe cards deck cannot be null");
        }
        if (model.getPlayers().size() < 2 || model.getPlayers().size() > 5) {
            throw new IllegalArgumentException("Number of players must be between 2 and 5");
        }
        if (model.getFinalEventCards() == null || model.getFinalEventCards().length != 2) {
            throw new IllegalArgumentException("Final event cards cannot be null and there must be 2 of them");
        }

        boolean newEraHasCome = false;

        int cardsToDraw = model.getPlayers().size() + 4 - topRow.size();

        if (cardsToDraw < 0) {
            throw new IllegalStateException("Cards to draw cannot be negative during top row population");
        }

        for (int i = 0; i < cardsToDraw; i++) {
            // when we reach the end of the last era deck we need to add the final event cards to the top row
            if (currentEra.equals(Era.ERA_III) && model.getTribeCardsDeck().get(currentEra).isEmpty()) {
                isEndGame = true;
                // if the deck is empty then we add to the top row the final event cards
                topRow.addLast(model.getFinalEventCards()[0]);
                topRow.addLast(model.getFinalEventCards()[1]);
                break;
            }

            if (model.getTribeCardsDeck().get(currentEra).isEmpty()) {
                newEraHasCome = true;
                currentEra = currentEra.nextEra();
                if (model.getTribeCardsDeck().get(currentEra) == null || model.getTribeCardsDeck().get(currentEra).isEmpty()) {
                    throw new IllegalStateException("Tribe cards deck for new current era cannot be null or empty during top row population (after new era has come)");
                }
            }

            TribeCard removedCard = model.getTribeCardsDeck().get(currentEra).removeLast();

            topRow.addLast(removedCard);
        }

        if (newEraHasCome) {
            // move the buildings from previous era to the bottom buildings row
            moveBuildingsFromTopToBottom();
            // populate the top building cards space with the cards from deck of the new current era
            populateTopBuildings();

            // TODO : comunicare inizio nuova era ...
        }

        if (isEndGame) {
            // TODO : comunicare end game ...
        }
    }

    /**
     *
     * Populate the bottom row of tribe cards. It is used only in the initialization phase of the board,
     * when we need to fill the bottom row with the first cards from the deck. It is guaranteed that there are
     * no event cards in the bottom row.
     *
     * @param model Game model
     * @throws IllegalArgumentException if the model is null, if the tribe cards deck is null or empty, or if the
     *                                  number of players is not between 2 and 5.
     * @throws IllegalStateException    if the bottom row is not empty during initialization, if the tribe cards
     *                                  deck is empty during the loop or if the top row is full while we try to
     *                                  initialize the bottom row.
     */
    private void populateBottomRow(GameModel model) throws IllegalArgumentException, IllegalStateException {
        // this method is used mainly in the initialization process of the board which
        // means that the bottom row cannot contain event cards

        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        if (model.getTribeCardsDeck() == null || model.getTribeCardsDeck().isEmpty() || model.getTribeCardsDeck().values().stream().allMatch(ArrayList::isEmpty)) {
            throw new IllegalArgumentException("Tribe cards deck cannot be null or empty during bottom row initialization");
        }
        if (model.getPlayers().size() < 2 || model.getPlayers().size() > 5) {
            throw new IllegalArgumentException("Number of players must be between 2 and 5");
        }
        if (!bottomRow.isEmpty()) {
            throw new IllegalStateException("Bottom row must be empty during bottom row initialization");
        }

        int cardsToDraw = model.getPlayers().size() + 1;
        int maxTopRowSize = model.getPlayers().size() + 4;

        while (bottomRow.size() < cardsToDraw) {
            // TODO  : we could force to pick Era.ERA_I cards instead
            if (model.getTribeCardsDeck().get(currentEra).isEmpty()) {
                throw new IllegalStateException("Tribe cards deck cannot be empty during bottom row initialization loop");
            }
            // TODO  : we could force to pick Era.ERA_I cards instead
            TribeCard removedCard = model.getTribeCardsDeck().get(currentEra).removeLast();

            try {
                removedCard.accept(new BottomRowInitVisitor(bottomRow, topRow, maxTopRowSize));
            } catch (IllegalStateException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    /**
     *
     * Moves the tribe cards from the top row to the bottom row. Should happen after the event resolution phase,
     * when the event cards are removed from the top row and the remaining tribe cards need to be moved to the bottom
     * row.
     */
    public void moveFromTopToBottom() {
        bottomRow.addAll(topRow);
        topRow.clear();
    }

    /**
     *
     * Remove the event cards from the bottom row and return them in a list sorted by priority (the ones with priority
     * last). This method should be used in the event resolution phase, when we need to resolve the events in the bottom
     * row.
     *
     * @return the list of event cards removed from the bottom row, sorted by priority.
     */
    public ArrayList<EventCard> cleanBottomRow() {
        ArrayList<EventCard> events = new ArrayList<>();
        EventListVisitor visitor = new EventListVisitor(events);

        for (TribeCard card : bottomRow) {
            card.accept(visitor);
        }

        events.sort(Comparator.comparing(EventCard::isLastToBeResolved).thenComparing(EventCard::getEra));

        // discard bottom row
        bottomRow.clear();

        return events;
    }

    /**
     *
     * Populate the top row of building cards with the cards from the deck of the current era. This method should be
     * used in the initialization phase of the board and when a new era starts, to populate the top row of building cards
     * with the cards from the new era.
     *
     * @throws IllegalStateException if the current era is null, if the buildings decks are null or if the building cards
     *                               deck for the current era is empty.
     */
    private void populateTopBuildings() throws IllegalStateException {
        if (currentEra == null) {
            throw new IllegalStateException("Current era cannot be null when populating the top buildings row");
        }
        if (buildingsDecks == null || buildingsDecks.get(currentEra) == null) {
            throw new IllegalStateException("Buildings decks cannot be null when populating the top buildings row");
        }
        if (buildingsDecks.get(currentEra).isEmpty()) {
            throw new IllegalStateException("Building cards deck for current era cannot be empty when populating the top buildings row");
        }

        topBuildings.clear();
        topBuildings.addAll(buildingsDecks.get(currentEra));
        buildingsDecks.get(currentEra).clear();
    }

    /**
     *
     * Move the building cards from the top row to the bottom row. This method should be used when a new era starts, to
     * move the remaining building cards from the previous era to the bottom row and make space for the new era building
     * cards in the top row.
     *
     */
    private void moveBuildingsFromTopToBottom() {
        bottomBuildings.clear();
        bottomBuildings.addAll(topBuildings);
        topBuildings.clear();
    }

    /**
     * This method is used to pick a card from the top row of cards. It handles the selection of a forbidden card (event card)
     * by the player and the selection of a character card, which is added to the player's hand and removed from the top row.
     * It is used in the offer resolution phase, when the player can pick one of the cards from the top row.
     *
     * @param player the player who is picking the card from the top row.
     * @param card   the card that the player is picking from the top row.
     * @throws IllegalArgumentException if the player is null, if the card is null, if the card is not found in the top
     *                                  row or if the card is an event card (which can't be picked from the player).
     */
    public void pickCardFromTopRow(Player player, TribeCard card) throws IllegalArgumentException {
    }

    public void pickCardFromTopRow(Player player, CharacterCard card) throws IllegalArgumentException {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        if (!topRow.contains(card)) {
            throw new IllegalArgumentException("Card not found in the top row");
        }

        notifyObserverBuildings(player);
        topRow.remove(card);
        player.addCharacterCards(card);
    }

    public void pickCardFromTopRow(Player player, EventCard card) throws IllegalArgumentException {
        throw new IllegalArgumentException("Cannot remove an event card from top row");
    }

    /**
     * This method is used to pick a card from the top bottom of cards. It handles the selection of a forbidden card (event card)
     * by the player and the selection of a character card, which is added to the player's hand and removed from the bottom row.
     * It is used in the offer resolution phase, when the player can pick one of the cards from the bottom row.
     *
     * @param player the player who is picking the card from the bottom row.
     * @param card   the card that the player is picking from the bottom row.
     * @throws IllegalArgumentException if the player is null, if the card is null, if the card is not found in the bottom
     *                                  row or if the card is an event card (which can't be picked from the player).
     */
    public void pickCardFromBottomRow(Player player, TribeCard card) throws IllegalArgumentException {
    }

    public void pickCardFromBottomRow(Player player, CharacterCard card) throws IllegalArgumentException {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        if (!bottomRow.contains(card)) {
            throw new IllegalArgumentException("Card not found in the bottom row");
        }

        notifyObserverBuildings(player);
        bottomRow.remove(card);
        player.addCharacterCards(card);
    }

    public void pickCardFromBottomRow(Player player, EventCard card) throws IllegalArgumentException {
        throw new IllegalArgumentException("Cannot remove an event card from bottom row");
    }

    /**
     * This method is used to buy a building card from the bottom row of building cards. It handles the buying logic,
     * by checking if the player has enough food tokens to buy the building card (considering the possible discount
     * from builder cards). It adds the building card to the player's hand and removes it from the top row of building
     * cards. It is used in the offer resolution phase, when the player can buy one of the building cards from the top row.
     *
     * @param player   the player who is buying the building card from the top row.
     * @param building the building card that the player is buying from the top row.
     * @throws IllegalArgumentException   if the player or the building card is null, if the building card is
     *                                    not found in the top row of building cards or if the player does not have enough
     *                                    food tokens to buy the building card
     * @throws IllegalGameActionException if the player does not have enough food tokens to buy the building card.
     */
    public void buyBuildingFromTopRow(Player player, BuildingCard building) throws IllegalArgumentException, IllegalGameActionException {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (building == null) {
            throw new IllegalArgumentException("Building Card cannot be null");
        }
        if (!topBuildings.contains(building)) {
            throw new IllegalArgumentException("Card not found in the top buildings row");
        }

        int finalCost = building.getFoodCost() - player.getBuildersDiscount();

        if (finalCost > player.getFoodTokens()) {
            throw new IllegalGameActionException("Player does not have enough food tokens to buy this building");
        }

        player.removeFoodTokens(finalCost);
        topBuildings.remove(building);
        player.addBuildingCards(building);
    }


    /**
     * This method is used to buy a building card from the bottom row of building cards. It handles the buying logic,
     * by checking if the player has enough food tokens to buy the building card (considering the possible discount
     * from builder cards). It adds the building card to the player's hand and removes it from the bottom row of building
     * cards. It is used in the offer resolution phase, when the player can buy one of the building cards from the bottom row.
     *
     * @param player   the player who is buying the building card from the bottom row.
     * @param building the building card that the player is buying from the bottom row.
     * @throws IllegalArgumentException   if the player or the building card is null, if the building card is
     *                                    not found in the bottom row of building cards
     * @throws IllegalGameActionException if the player does not have enough food tokens to buy the building card.
     */
    public void buyBuildingFromBottomRow(Player player, BuildingCard building) throws IllegalArgumentException, IllegalGameActionException {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (building == null) {
            throw new IllegalArgumentException("Building Card cannot be null");
        }
        if (!bottomBuildings.contains(building)) {
            throw new IllegalArgumentException("Card not found in the bottom buildings row");
        }

        int finalCost = building.getFoodCost() - player.getBuildersDiscount();

        if (finalCost > player.getFoodTokens()) {
            throw new IllegalGameActionException("Player does not have enough food tokens to buy this building");
        }

        player.removeFoodTokens(finalCost);
        bottomBuildings.remove(building);
        player.addBuildingCards(building);
    }

    public boolean isEndGame() {
        return isEndGame;
    }

    /**
     * this method is used to add an observer to be notified during the card drawing process.
     * It checks if the observer is already in the list
     *
     * @param observer the observer to add.
     */
    @Override
    public void addObserver(DrawObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * this method is used to remove an observer from the notification list.
     *
     * @param observer the observer to remove.
     */
    @Override
    public void removeObserver(DrawObserver observer) {
        observers.remove(observer);
    }

    /**
     * this method is used to notify the observers during the card drawing process.
     * It is called when a player picks a card from the top or bottom row,
     * to notify the observers of the change in the player's hand and the possible effects of the card picked.
     *
     * @param player the player who is picking the card from the top or bottom row, to notify
     *               the observers of the change in the player's hand and the possible effects of the card picked.
     */
    @Override
    public void notifyObserverBuildings(Player player) {
        observers.forEach(observer -> observer.update(player));
    }
}
