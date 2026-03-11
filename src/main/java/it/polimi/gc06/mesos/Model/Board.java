package it.polimi.gc06.mesos.Model;
import java.util.ArrayList;

public class Board {
    private ArrayList<TribeCard> topRow;
    private ArrayList<TribeCard> bottomRow;

    private ArrayList<BuildingCard> topBuildings;
    private ArrayList<BuildingCard> bottomBuildings;

    private ArrayList<ArrayList<BuildingCard>> buildingsDecks;

    private Era currentEra;

    // TODO : the getters returns the reference to the objects, should we return a
    // a copy of some of them which should not be modified externally?

    public Board() {
        topRow = new ArrayList<TribeCard>();
        bottomRow = new ArrayList<TribeCard>();

        topBuildings = new ArrayList<BuildingCard>();
        bottomBuildings = new ArrayList<BuildingCard>();

        buildingsDecks = new ArrayList<ArrayList<BuildingCard>>(3);
        for (int i = 0; i < 3; i++) {
            buildingsDecks.add(new ArrayList<BuildingCard>());
        }

        currentEra = Era.ERA_I;
    }

    /**
     * {@inheritDoc}
     * @return The top row of tribe cards ArrayList.
     */
    public ArrayList<TribeCard> getTopRow() {
        return topRow;
    }

    /**
     * {@inheritDoc}
     * @return The bottom row of tribe cards ArrayList.
     */
    public ArrayList<TribeCard> getBottomRow() {
        return bottomRow;
    }

    /**
     * {@inheritDoc}
     * @return The top row of building cards space ArrayList.
     */
    public ArrayList<BuildingCard> getTopBuildings() {
        return topBuildings;
    }

    /**
    * {@inheritDoc}
    * @return The bottom row of building cards space ArrayList.
    */
    public ArrayList<BuildingCard> getBottomBuildings() {
        return bottomBuildings;
    }

    /**
     * {@inheritDoc}
     * @return The decks of building cards which are not active yet (so whose era is yet to come).
     */
    public ArrayList<ArrayList<BuildingCard>> getBuildingsDecks() {
        return buildingsDecks;
    }

    /**
     * {@inheritDoc}
     * @return The current era of the game.
     */
    public Era getCurrentEra() {
        return currentEra;
    }

    /**
     * {@inheritDoc}
     * Initialize the board with the cards from the model.
     */
    protected void initBoard(GameModel model)  {
        if(model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        if(model.getTribeCardsDeck() == null || model.getTribeCardsDeck().isEmpty()) {
            throw new IllegalArgumentException("Tribe cards deck cannot be null or empty");
        }
        if(model.getBuildingCardsDecks() == null || model.getBuildingCardsDecks().isEmpty()) {
            throw new IllegalArgumentException("Building cards decks cannot be null or empty");
        }
        if(model.getPlayers().size() < 2 || model.getPlayers().size() > 5) {
            throw new IllegalArgumentException("Number of players must be between 2 and 5");
        }
        if(model.getFinalEventCards() == null || model.getFinalEventCards().length != 2) {
            throw new IllegalArgumentException("Final event cards cannot be null and there must be 2 of them");
        }

        // rules state that the initialization of the board is done in this order:
        // initialize bottom row -> initialize top row -> create building decks -> initialize top buildings row

        // (ma inizializziamo prima i mazzetti dei buildings perché nel caso di new era durante i test con
        // poche carte potrebbe essere necessario spostare dei buildings dalla top row alla bottom row e quindi
        // è meglio avere già i mazzetti pronti)

        // create the decks of buildings cards
        // the rules specify the number of the buildings on the top row based on the number of player and Era
        int[][] nBuildings = {{1,2,3},{2,2,4},{2,3,4},{2,3,5}};

        for (int i = 0; i < buildingsDecks.size(); i++) {
            for (int j = 0; j < nBuildings[model.getPlayers().size() - 2][i]; j++) {
                // add to the board deck 'i' the card removed from the deck 'i' of the model
                buildingsDecks.get(i).addFirst(model.getBuildingCardsDecks().get(i).removeFirst());
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
        if(model.getTribeCardsDeck() == null) {
            throw new IllegalArgumentException("Tribe cards deck cannot be null or empty");
        }
        if(model.getPlayers().size() < 2 || model.getPlayers().size() > 5) {
            throw new IllegalArgumentException("Number of players must be between 2 and 5");
        }
        if(model.getFinalEventCards() == null || model.getFinalEventCards().length != 2) {
            throw new IllegalArgumentException("Final event cards cannot be null and there must be 2 of them");
        }
        if (topRow == null) {
            throw new IllegalStateException("Top row cannot be null during top row initialization");
        }

        boolean newEraHasCome = false;

        int cardsToDraw = model.getPlayers().size() + 4 - topRow.size();

        for (int i = 0; i < cardsToDraw; i++) {
            if(model.getTribeCardsDeck().isEmpty()) {
                // if the deck is empty then we add to the top row the final event cards
                topRow.addFirst(model.getFinalEventCards()[0]);
                topRow.addFirst(model.getFinalEventCards()[1]);
                break;
            }

            TribeCard removedCard = model.getTribeCardsDeck().removeFirst();

            // should this be removedCard.Era > currentEra?
            if(removedCard.getEra().ordinal() > currentEra.ordinal()) {
                newEraHasCome = true;
            }
            topRow.addFirst(removedCard);
        }

        if (newEraHasCome) {
            // increase the era
            currentEra = currentEra.nextEra();
            // move the buildings from previous era to the bottom buildings row
            moveBuildingsFromTopTopBottom();
            // populate the top building cards space with the cards from deck of the new current era
            populateTopBuildings();

            // TODO : comunicare inizio nuova era ...
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
        if(model.getTribeCardsDeck() == null || model.getTribeCardsDeck().isEmpty()) {
            throw new IllegalArgumentException("Tribe cards deck cannot be null or empty during bottom row initialization");
        }
        if(model.getPlayers().size() < 2 || model.getPlayers().size() > 5) {
            throw new IllegalArgumentException("Number of players must be between 2 and 5");
        }
        if (bottomRow == null) {
            throw new IllegalStateException("Bottom row cannot be null during bottom row initialization");
        }

        int cardsToDraw = model.getPlayers().size() + 4 - bottomRow.size();

        for (int i = 0; i < cardsToDraw; i++) {
            if(model.getTribeCardsDeck().isEmpty()) {
                throw new IllegalStateException("Tribe cards deck cannot be empty during bottom row initialization loop");
            }
            TribeCard removedCard = model.getTribeCardsDeck().removeFirst();

            // check if the card is an event
            if (removedCard.isEventCard()) {
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
        if(topRow == null) {
            throw new IllegalStateException("Top row cannot be null when moving cards to the bottom row");
        }
        if(bottomRow == null) {
            throw new IllegalStateException("Bottom row cannot be null when moving cards to the bottom row");
        }

        if(bottomRow.isEmpty()) {
            bottomRow = topRow;
            topRow = new ArrayList<TribeCard>();
        } else {
            bottomRow.addAll(topRow);
            topRow.clear();
        }

    }

    /**
     * {@inheritDoc}
     * Clear the bottom tribe cards row.
     */
    protected void discardBottomRow() {
        bottomRow.clear();
    }

    /**
     * {@inheritDoc}
     * Populate the top building cards row
     */
    protected void populateTopBuildings() {
        topBuildings = buildingsDecks.get(currentEra.ordinal());
        buildingsDecks.set(currentEra.ordinal(), null);
    }

    /**
     * {@inheritDoc}
     * Moves the building cards from the top row to the bottom row.
     */
    protected void moveBuildingsFromTopTopBottom() {
        if (topBuildings == null) {
            throw new IllegalStateException("Top buildings cannot be null when moving cards to the bottom buildings row");
        }
        if(bottomBuildings == null) {
            throw new IllegalStateException("Bottom buildings cannot be null when moving cards to the bottom buildings row");
        }

        bottomBuildings = topBuildings;
        topBuildings = new ArrayList<BuildingCard>();
    }

    /**
     * {@inheritDoc}
     * Clears the bottom building cards row.
     */
    protected void discardBottomBuildings() {
        if(bottomBuildings == null) {
            throw new IllegalStateException("Bottom buildings cannot be null when discarding the bottom buildings row");
        }

        bottomBuildings.clear();
    }

    /**
     * {@inheritDoc}
     * Removes the specified card from the top row of tribe cards.
    */
    protected void removeCardFromTop(TribeCard card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        if (topRow == null) {
            throw new IllegalStateException("Top row cannot be null when removing a card from the top row");
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
    protected void removeCardFromBottom(TribeCard card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        if (bottomRow == null) {
            throw new IllegalStateException("Bottom row cannot be null when removing a card from the bottom row");
        }
        if (!bottomRow.contains(card)) {
            throw new IllegalArgumentException("Card not found in the bottom row");
        }
//        if (card.isEventCard()) {
//            throw new IllegalArgumentException("Cannot remove an event card");
//        }

        bottomRow.remove(card);
    }

    // should we have a remove top row (or bottom row) method based on the index of the card on the list?
}
