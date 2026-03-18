package it.polimi.gc06.mesos.Model.GameBoard;

import it.polimi.gc06.mesos.Model.Cards.Buildings.BuildingCard;
import it.polimi.gc06.mesos.Model.Cards.Events.EventCard;
import it.polimi.gc06.mesos.Model.Cards.TribeCard;
import it.polimi.gc06.mesos.Model.Era;
import it.polimi.gc06.mesos.Model.GameModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.stream.Collectors;

public class Board {
    private final ArrayList<TribeCard> topRow;
    private final ArrayList<TribeCard> bottomRow;

    private final ArrayList<BuildingCard> topBuildings;
    private final ArrayList<BuildingCard> bottomBuildings;

    private final EnumMap<Era, ArrayList<BuildingCard>> buildingsDecks;

    private Era currentEra;

    public Board() {
        topRow = new ArrayList<>();
        bottomRow = new ArrayList<>();

        topBuildings = new ArrayList<>();
        bottomBuildings = new ArrayList<>();

        buildingsDecks = new EnumMap<>(Era.class);
        for (Era era : Era.values()) {
            buildingsDecks.put(era, new ArrayList<>());
        }

        currentEra = Era.ERA_I;
    }

    /**
     *
     * Return the top row of tribe cards.
     *
     * @return the array list of tribe cards in the top row.
     */
    protected ArrayList<TribeCard> getTopRow() {
        return topRow;
    }

    /**
     *
     * Return the bottom row of tribe cards.
     *
     * @return the array list of tribe cards in the bottom row.
     */
    public ArrayList<TribeCard> getBottomRow() {
        return bottomRow;
    }

    /**
     *
     * Return the top row of building cards.
     *
     * @return the array list of building cards in the top building space.
     */
    protected ArrayList<BuildingCard> getTopBuildings() {
        return topBuildings;
    }

    /**
     *
     * Return the bottom row of building cards.
     *
     * @return the array list of building cards in the bottom building space.
     */
    protected ArrayList<BuildingCard> getBottomBuildings() {
        return bottomBuildings;
    }

    /**
     *
     * Return the decks of building cards for each era.
     *
     * @return the enum map of building cards decks based on their Era.
     */
    protected EnumMap<Era, ArrayList<BuildingCard>> getBuildingsDecks() {
        return buildingsDecks;
    }

    /**
     *
     * Return the current era of the game.
     *
     * @return the enum value of current era.
     */
    protected Era getCurrentEra() {
        return currentEra;
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
    protected void initBoard(GameModel model) throws IllegalArgumentException, IllegalStateException {
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

        // create the decks of buildings cards
        // the rules specify the number of the buildings on the top row based on the number of player and Era,
        // which are respectively the columns and the rows of the matrix.
        int[][] nBuildings = {{1, 2, 3}, {2, 2, 4}, {2, 3, 4}, {2, 3, 5}};


        for (Era era : Era.values()) {
            for (int j = 0; j < nBuildings[model.getPlayers().size() - 2][era.ordinal()]; j++) {
                if (model.getBuildingCardsDecks().get(era) == null || model.getBuildingCardsDecks().get(era).isEmpty()) {
                    throw new IllegalStateException("Building cards deck for era " + era + " cannot be null or empty during building decks initialization");
                }
                buildingsDecks.get(era).addFirst(model.getBuildingCardsDecks().get(era).removeFirst());
            }
        }

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
        boolean isEndGame = false;

        int cardsToDraw = model.getPlayers().size() + 4 - topRow.size();

        if (cardsToDraw < 0) {
            throw new IllegalStateException("Cards to draw cannot be negative during top row population");
        }

        for (int i = 0; i < cardsToDraw; i++) {
            // when we reach the end of the last era deck we need to add the final event cards to the top row
            if (currentEra.equals(Era.ERA_III) && model.getTribeCardsDeck().get(currentEra).isEmpty()) {
                isEndGame = true;
                // if the deck is empty then we add to the top row the final event cards
                topRow.addFirst(model.getFinalEventCards()[0]);
                topRow.addFirst(model.getFinalEventCards()[1]);
                break;
            }

            if (model.getTribeCardsDeck().get(currentEra).isEmpty()) {
                newEraHasCome = true;
                // increase the era
                currentEra = currentEra.nextEra();
                if (model.getTribeCardsDeck().get(currentEra) == null || model.getTribeCardsDeck().get(currentEra).isEmpty()) {
                    throw new IllegalStateException("Tribe cards deck for new current era cannot be null or empty during top row population (after new era has come)");
                }
            }

            TribeCard removedCard = model.getTribeCardsDeck().get(currentEra).removeFirst();

            topRow.addFirst(removedCard);
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

        for (int i = 0; i < cardsToDraw; i++) {
            // TODO  : we could force to pick Era.ERA_I cards instead
            if (model.getTribeCardsDeck().get(currentEra).isEmpty()) {
                throw new IllegalStateException("Tribe cards deck cannot be empty during bottom row initialization loop");
            }
            // TODO  : we could force to pick Era.ERA_I cards instead
            TribeCard removedCard = model.getTribeCardsDeck().get(currentEra).removeFirst();

            // check if the card is an event
            if (removedCard.isEventCard()) {
                if (topRow.size() >= maxTopRowSize) {
                    //this happens just if we draw (4 + # players) event cards during this phase, which we hope is unlikely to happen
                    throw new IllegalStateException("Top row cannot contain more than " + maxTopRowSize + " cards during bottom row initialization");
                }
                topRow.addFirst(removedCard);
                i--;
            } else {
                bottomRow.addFirst(removedCard);
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
        // we can safely cast the cards to event cards because we know that the bottom row can
        // contain only character cards and event cards, and we are filtering only the event cards
        ArrayList<EventCard> events = bottomRow.stream()
                .filter(TribeCard::isEventCard)
                .map(card -> (EventCard) card)
                .sorted(Comparator.comparing(EventCard::isLastToBeResolved))
                .collect(Collectors.toCollection(ArrayList::new));

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
     *
     * Removes the specified card from the top row of tribe cards. It is used to pick a card from the top row during
     * the offer resolution phase, when the player can pick one of the cards from the top row, and we need to
     * remove it from the board.
     *
     * @param card the card to be removed from the top row.
     * @throws IllegalArgumentException if the card is null, if the card is not found in the top row or if the card is
     *                                  an event card (which can't be picked from the player).
     */
    public void removeTribeCardFromTopRow(TribeCard card) throws IllegalArgumentException {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        if (!topRow.contains(card)) {
            throw new IllegalArgumentException("Card not found in the top row");
        }
        if (card.isEventCard()) {
            throw new IllegalArgumentException("Cannot remove an event card form top row");
        }

        topRow.remove(card);
    }

    /**
     *
     * Removes the specified card from the bottom row of tribe cards. It is used to pick a card from the bottom row during
     * the event resolution phase, when the player can pick one of the cards from the bottom row, and we need to
     * remove it from the board.
     *
     * @param card the card to be removed from the bottom row.
     * @throws IllegalArgumentException if the card is null, if the card is not found in the bottom row or if the card is
     *                                  an event card (which can't be picked from the player).
     */
    public void removeTribeCardFromBottomRow(TribeCard card) throws IllegalArgumentException {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        if (!bottomRow.contains(card)) {
            throw new IllegalArgumentException("Card not found in the bottom row");
        }
        if (card.isEventCard()) {
            throw new IllegalArgumentException("Cannot remove an event card form bottom row");
        }

        bottomRow.remove(card);
    }

    /**
     *
     * Removes the specified card from the top row of buildings cards. It is used to pick a building card from the top
     * row during the offer resolution phase, when the player decide to buy a building, and we need to
     * remove it from the board.
     *
     * @param card the card to be removed from the top row.
     * @throws IllegalArgumentException if the card is null, if the card is not found in the top buildings row or if the
     *                                  card is an event card (which can't be picked from the player).
     */
    public void removeBuildingCardFromTopRow(BuildingCard card) throws IllegalArgumentException {
        if (card == null) {
            throw new IllegalArgumentException("Building Card cannot be null");
        }
        if (!topBuildings.contains(card)) {
            throw new IllegalArgumentException("Card not found in the top buildings row");
        }

        // TODO : the buying logic should be handled here ??

        topBuildings.remove(card);
    }

    /**
     *
     * Removes the specified card from the bottom row of buildings cards. It is used to pick a building card from the
     * bottom row during the event resolution phase, when the player decide to buy a building, and we need to
     * remove it from the board.
     *
     * @param card the card to be removed from the bottom row.
     * @throws IllegalArgumentException if the card is null, if the card is not found in the bottom buildings row or if
     *                                  the card is an event card (which can't be picked from the player).
     */
    public void removeBuildingCardFromBottomRow(BuildingCard card) throws IllegalArgumentException {
        if (card == null) {
            throw new IllegalArgumentException("Building Card cannot be null");
        }
        if (!bottomBuildings.contains(card)) {
            throw new IllegalArgumentException("Card not found in the bottom buildings row");
        }

        // TODO : the buying logic should be handled here ??

        bottomBuildings.remove(card);
    }
}
