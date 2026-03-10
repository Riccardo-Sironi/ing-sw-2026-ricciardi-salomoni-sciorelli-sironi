package it.polimi.gc06.mesos.Model;
import java.util.ArrayList;

public class Board {
    private ArrayList<TribeCard> topRow;
    private ArrayList<TribeCard> bottomRow;

    private ArrayList<BuildingCard> topBuildings;
    private ArrayList<BuildingCard> bottomBuildings;

    private ArrayList<ArrayList<BuildingCard>> buildingsDecks;

    // WIP
//    private ArrayList<BuildingCard> eraIBuildingsDeck;
//    private ArrayList<BuildingCard> eraIIBuildingsDeck;
//    private ArrayList<BuildingCard> eraIIIBuildingsDeck;

    private Era currentEra;

    /*  */

    public Board(GameModel model) {
        topRow = new ArrayList<TribeCard>();
        bottomRow = new ArrayList<TribeCard>();

        topBuildings = new ArrayList<BuildingCard>();
        bottomBuildings = new ArrayList<BuildingCard>();

        buildingsDecks = new ArrayList<ArrayList<BuildingCard>>(3);

        currentEra = Era.ERA_I;
    }

    public ArrayList<TribeCard> getTopRow() {
        return topRow;
    }

    public ArrayList<TribeCard> getBottomRow() {
        return bottomRow;
    }

    public ArrayList<BuildingCard> getTopBuildings() {
        return topBuildings;
    }

    public ArrayList<BuildingCard> getBottomBuildings() {
        return bottomBuildings;
    }

//    public ArrayList<BuildingCard> getEraIBuildingsDeck() {
//        return eraIBuildingsDeck;
//    }
//
//    public ArrayList<BuildingCard> getEraIIBuildingsDeck() {
//        return eraIIBuildingsDeck;
//    }
//
//    public ArrayList<BuildingCard> getEraIIIBuildingsDeck() {
//        return eraIIIBuildingsDeck;
//    }

    public Era getCurrentEra() {
        return currentEra;
    }

    /**
     * {@inheritDoc}
     * @param model Game model
     * @return
     */
    protected boolean initBoard(GameModel model) {
        // first and only initialization of bottom row tribe cards
        populateBottomRow(model);

        // top row initialization (there could be some events cards already)
        populateTopRow(model);

        // create the decks of buildings cards
        // the rules specify the number of the buildings on the top row based on the number of player and Era
        int[][] nBuildings = {{1,2,3},{2,2,4},{2,3,4},{2,3,5}};

        for (int i = 0; i < buildingsDecks.size(); i++) {
            for (int j = 0; j < nBuildings[model.getPlayers().size() - 2][i]; j++) {
                // add to the board deck 'i' the card removed from the deck 'i' of the model
                buildingsDecks.get(i).addFirst(model.getBuildingCardsDecks().get(i).removeFirst());
            }
        }

        // populate the top building cards space with the cards from deck of the current era (ERA_I in this case)
        populateTopBuildings();

        return true;
    };

    /**
     * {@inheritDoc}
     * Populate the top row of tribe cards.
     * @param model Game model
     * @return
     */
    protected boolean populateTopRow(GameModel model) {
        // we need to subtract the toprow.size() for initialization purposes
        // (in the first round it's forbidden to have events in the bottom row so we
        // move them from the bottom to the top)

        for (int i = 0; i < model.getPlayers().size() + 4 - topRow.size(); i++) {
            TribeCard removedCard = model.getTribeCardsDeck().removeFirst();

            topRow.addFirst(removedCard);
        }

        return true;
    };

    /**
     * {@inheritDoc}
     * Populate the bottom row of tribe cards.
     * @param model Game model
     * @return
     */
    protected boolean populateBottomRow(GameModel model) {
        // this method is used mainly in the initialization process of the board which
        // means that the bottom row cannot contain event cards
        for (int i = 0; i < model.getPlayers().size() + 4 - bottomRow.size(); i++) {
            TribeCard removedCard = model.getTribeCardsDeck().removeFirst();

            // check if the card is not an event
            if (removedCard instanceof EventCard) {
                topRow.addFirst(removedCard);
                i--;
            } else {
                bottomRow.addFirst(removedCard);
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * Move the cards from the top row to the bottom row.
     * @return
     */
    protected boolean moveFromTopToBottom() {
        bottomRow = topRow;
        topRow = null;
        topRow = new ArrayList<TribeCard>();

        return true;
    };

    /**
     * {@inheritDoc}
     * Clear the bottom tribe cards row.
     * @return
     */
    protected boolean discardBottomRow() {
        bottomRow.clear();

        return true;
    };

    /**
     * {@inheritDoc}
     * Populate the top building cards row
     * @return
     */
    protected boolean populateTopBuildings() {
        topBuildings = buildingsDecks.get(currentEra.ordinal());
        buildingsDecks.set(currentEra.ordinal(), null);

        return true;
    };

    /**
     * {@inheritDoc}
     * Moves the building cards from the top row to the bottom row.
     * @return
     */
    protected boolean moveBuildingsFromTopTopBottom() {
        bottomBuildings = topBuildings;
        topBuildings = null;
        topBuildings = new ArrayList<BuildingCard>();

        return  true;
    };

    /**
     * {@inheritDoc}
     * Clears the bottom building cards row.
     * @return
     */
    protected boolean discardBottomBuildings() {
        bottomBuildings.clear();

        return true;
    };

    protected boolean removeCardFromTop() {return true;};
    protected boolean removeCardFromBottom() {return true;};
}
