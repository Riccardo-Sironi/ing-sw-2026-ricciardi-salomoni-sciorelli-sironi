package it.polimi.gc06.mesos.Model;
import java.util.*;

public class Board {
    private final ArrayList<TribeCard> topRow;
    private final ArrayList<TribeCard> bottomRow;

    private final ArrayList<BuildingCard> topBuildings;
    private final ArrayList<BuildingCard> bottomBuildings;

    private final EnumMap<Era,ArrayList<BuildingCard>> buildingsDecks;

    private Era currentEra;

    public Board() {
        topRow = new ArrayList<TribeCard>();
        bottomRow = new ArrayList<TribeCard>();

        topBuildings = new ArrayList<BuildingCard>();
        bottomBuildings = new ArrayList<BuildingCard>();

        buildingsDecks = new EnumMap<>(Era.class);
        for (Era era : Era.values()) {
            buildingsDecks.put(era, new ArrayList<BuildingCard>());
        }

        currentEra = Era.ERA_I;
    }

    /**
     * {@inheritDoc}
     * @return The top row of tribe cards ArrayList.
     */
    protected ArrayList<TribeCard> getTopRow() {
        return topRow;
    }

    /**
     * {@inheritDoc}
     * @return The bottom row of tribe cards ArrayList.
     */
    protected ArrayList<TribeCard> getBottomRow() {
        return bottomRow;
    }

    /**
     * {@inheritDoc}
     * @return The top row of building cards space ArrayList.
     */
    protected ArrayList<BuildingCard> getTopBuildings() {
        return topBuildings;
    }

    /**
    * {@inheritDoc}
    * @return The bottom row of building cards space ArrayList.
    */
    protected ArrayList<BuildingCard> getBottomBuildings() {
        return bottomBuildings;
    }

    /**
     * {@inheritDoc}
     * @return The decks of building cards which are not active yet (so whose era is yet to come).
     */
    protected EnumMap<Era, ArrayList<BuildingCard>> getBuildingsDecks() {
        return buildingsDecks;
    }

    /**
     * {@inheritDoc}
     * @return The current era of the game.
     */
    protected Era getCurrentEra() {
        return currentEra;
    }

    /**
     * {@inheritDoc}
     * Initialize the board with the cards from the model.
     */
    protected void initBoard(GameModel model) throws IllegalArgumentException, IllegalStateException {
        if(model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        if(model.getTribeCardsDeck() == null || model.getTribeCardsDeck().isEmpty() || model.getTribeCardsDeck().values().stream().allMatch(ArrayList::isEmpty)) {
            throw new IllegalArgumentException("Tribe cards deck cannot be null or empty");
        }
        if(model.getBuildingCardsDecks() == null || model.getBuildingCardsDecks().isEmpty() || model.getBuildingCardsDecks().values().stream().allMatch(ArrayList::isEmpty)) {
            throw new IllegalArgumentException("Building cards decks cannot be null or empty");
        }
        if(model.getPlayers().size() < 2 || model.getPlayers().size() > 5) {
            throw new IllegalArgumentException("Number of players must be between 2 and 5");
        }
        if(model.getFinalEventCards() == null || model.getFinalEventCards().length != 2) {
            throw new IllegalArgumentException("Final event cards cannot be null and there must be 2 of them");
        }

        // create the decks of buildings cards
        // the rules specify the number of the buildings on the top row based on the number of player and Era,
        // which are respectively the columns and the rows of the matrix.
        int[][] nBuildings = {{1,2,3},{2,2,4},{2,3,4},{2,3,5}};


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
     * {@inheritDoc}
     * Populate the top row of tribe cards.
     * @param model Game model
     */
    protected void populateTopRow(GameModel model) {
        // we need to subtract the toprow.size() for initialization purposes
        // (in the first round it's forbidden to have events in the bottom row so we
        // move them from the bottom to the top)

        if(model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        if(model.getTribeCardsDeck() == null || model.getTribeCardsDeck().get(currentEra) == null) {
            throw new IllegalArgumentException("Tribe cards deck cannot be null");
        }
        if(model.getPlayers().size() < 2 || model.getPlayers().size() > 5) {
            throw new IllegalArgumentException("Number of players must be between 2 and 5");
        }
        if(model.getFinalEventCards() == null || model.getFinalEventCards().length != 2) {
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
            if(currentEra.equals(Era.ERA_III) && model.getTribeCardsDeck().get(currentEra).isEmpty()) {
                isEndGame = true;
                // if the deck is empty then we add to the top row the final event cards
                topRow.addFirst(model.getFinalEventCards()[0]);
                topRow.addFirst(model.getFinalEventCards()[1]);
                break;
            }

            if(model.getTribeCardsDeck().get(currentEra).isEmpty()) {
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
     * {@inheritDoc}
     * Populate the bottom row of tribe cards.
     * @param model Game model
     */
    protected void populateBottomRow(GameModel model) {
        // this method is used mainly in the initialization process of the board which
        // means that the bottom row cannot contain event cards

        if(model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        if(model.getTribeCardsDeck() == null || model.getTribeCardsDeck().isEmpty() || model.getTribeCardsDeck().values().stream().allMatch(ArrayList::isEmpty)) {
            throw new IllegalArgumentException("Tribe cards deck cannot be null or empty during bottom row initialization");
        }
        if(model.getPlayers().size() < 2 || model.getPlayers().size() > 5) {
            throw new IllegalArgumentException("Number of players must be between 2 and 5");
        }
        if(!bottomRow.isEmpty()) {
            throw new IllegalStateException("Bottom row must be empty during bottom row initialization");
        }

        int cardsToDraw = model.getPlayers().size() + 1;
        int maxTopRowSize = model.getPlayers().size() + 4;

        for (int i = 0; i < cardsToDraw; i++) {
            // TODO  : we could force to pick Era.ERA_I cards instead
            if(model.getTribeCardsDeck().get(currentEra).isEmpty()) {
                throw new IllegalStateException("Tribe cards deck cannot be empty during bottom row initialization loop");
            }
            // TODO  : we could force to pick Era.ERA_I cards instead
            TribeCard removedCard = model.getTribeCardsDeck().get(currentEra).removeFirst();

            // check if the card is an event
            if (removedCard.isEventCard()) {
                if(topRow.size() >= maxTopRowSize) {
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
     * {@inheritDoc}
     * Move the cards from the top row to the bottom row.
     */
    protected void moveFromTopToBottom() {
//        if (topRow.isEmpty()) {
//            throw new IllegalStateException("Top row cannot be empty when moving cards to the bottom row");
//        }
        bottomRow.addAll(topRow);
        topRow.clear();
    }

    /**
     *
     */
    protected ArrayList<EventCard> cleanBottomRow() {
        // we can safely cast the cards to event cards because we know that the bottom row can
        // contain only character cards and event cards, and we are filtering only the event cards
        ArrayList<EventCard> events = (ArrayList<EventCard>) bottomRow.stream()
                .filter(TribeCard::isEventCard)
                .map(card -> (EventCard) card)
                .sorted(Comparator.comparing(EventCard::hasPriority).reversed())
                .toList();

        // discard bottom row
        bottomRow.clear();

        return events;
    }

    /**
     * {@inheritDoc}
     * Populate the top building cards row
     */
    protected void populateTopBuildings() {
        if (currentEra == null) {
            throw new IllegalStateException("Current era cannot be null when populating the top buildings row");
        }
        if (buildingsDecks == null || buildingsDecks.get(currentEra) == null) {
            throw new IllegalStateException("Buildings decks cannot be null when populating the top buildings row");
        }
        if(buildingsDecks.get(currentEra).isEmpty()) {
            throw new IllegalStateException("Building cards deck for current era cannot be empty when populating the top buildings row");
        }

        topBuildings.clear();
        topBuildings.addAll(buildingsDecks.get(currentEra));
        buildingsDecks.get(currentEra).clear();
    }

    /**
     * {@inheritDoc}
     * Moves the building cards from the top row to the bottom row.
     */
    protected void moveBuildingsFromTopToBottom() {
        bottomBuildings.clear();
        bottomBuildings.addAll(topBuildings);
        topBuildings.clear();
    }

    /**
     * {@inheritDoc}
     * Removes the specified card from the top row of tribe cards.
    */
    protected void removeTribeCardFromTopRow(TribeCard card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        if (!topRow.contains(card)) {
            throw new IllegalArgumentException("Card not found in the top row");
        }
//        if (card.isEventCard()) {
//            throw new IllegalArgumentException("Cannot remove an event card");
//        }

        topRow.remove(card);
    }

    /**
     * {@inheritDoc}
     * Removes the specified card from the bottom row of tribe cards.
     */
    protected void removeTribeCardFromBottomRow(TribeCard card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        if (!bottomRow.contains(card)) {
            throw new IllegalArgumentException("Card not found in the bottom row");
        }
//        if (card.isEventCard()) {
//            throw new IllegalArgumentException("Cannot remove an event card");
//        }

        bottomRow.remove(card);
    }

    /**
     * {@inheritDoc}
     * Removes the specified card from the top row of buildings cards.
     */
    protected void removeBuildingCardFromTopRow(BuildingCard card) {
        if (card == null) {
            throw new IllegalArgumentException("Building Card cannot be null");
        }
        if (!topBuildings.contains(card)) {
            throw new IllegalArgumentException("Card not found in the top buildings row");
        }

        topBuildings.remove(card);
    }

    /**
     * {@inheritDoc}
     * Removes the specified card from the bottom row of buildings cards.
     */
    protected void removeBuildingCardFromBottomRow(BuildingCard card) {
        if (card == null) {
            throw new IllegalArgumentException("Building Card cannot be null");
        }
        if (!bottomBuildings.contains(card)) {
            throw new IllegalArgumentException("Card not found in the bottom buildings row");
        }

        bottomBuildings.remove(card);
    }
}
