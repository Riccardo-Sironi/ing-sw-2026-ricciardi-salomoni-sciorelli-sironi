package it.polimi.gc06.mesos.controller;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;

import java.util.List;

public class GameController {
    private final GameModel model;

    public GameController(GameModel model) {
        this.model = model;
    }

    public GameModel getModel() {
        return model;
    }

    //initialization should be handled server side, it's not a request from the player
    /*public void handleGameInitialization() {
        model.startGame();
    }*/

    /**
     * Handles the request of tile placement
     *
     * @param playerNickname the player performing the action.
     * @param tileIndex the index of the tile where the player wants to be placed.
     */
    public void handleTotemOfferTilePlacement(String playerNickname, int tileIndex) {
        TurnManager turnManager = model.getTurnManager();
        try {
            Player activePlayer = turnManager.getActivePlayer();

            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("It is not " + playerNickname + " turn !");
            }

            List<TileSlot> offerTrack = model.getBoard().getOfferTrack();
            if (tileIndex >= offerTrack.size() || tileIndex < 0){
                throw new IllegalPhaseActionException("Requested tile does not exist !");
            }
            TileSlot tile = offerTrack.get(tileIndex);

            if (tile.getPlayer() != null) {
                throw new IllegalPhaseActionException("The tile is already occupied !");
            }

            turnManager.getPhase().placeTotem(turnManager, activePlayer, tile);
        } catch (IllegalPhaseActionException e) {
            System.err.println("Error for " + playerNickname + ": " + e.getMessage()); // TODO : communicate the error to the view
        }
    }

    /**
     * Handles the request of card picking from bottom row.
     *
     * @param playerNickname the player performing the action.
     * @param cardIndex the index of the card that the player wants to pick.
     */
    public void handleCardPickBottomRow(String playerNickname, int cardIndex) {
        TurnManager turnManager = model.getTurnManager();

        try {
            Player activePlayer = turnManager.getActivePlayer();
            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
            }

            TribeCard card = model.getBoard().getBottomCardFromIndex(cardIndex);

            CardVisitor cardPickerVisitor = new CardVisitor(){
                @Override
                public void visit(CharacterCard card){
                    turnManager.getPhase().pickCardFromBottom(turnManager, activePlayer, card, model.getBoard());
                }
                @Override
                public void visit(EventCard card) throws IllegalGameActionException {
                    throw new IllegalGameActionException("Event cards cannot be picked!");
                }
            };

            card.accept(cardPickerVisitor);

        } catch (IllegalGameActionException | IndexOutOfBoundsException e) {
            System.err.println("Error for " + playerNickname + ": " + e.getMessage());
            // TODO: Inviare un pacchetto di Errore al Client
        }
    }

    /**
     * Handles the request of card picking from top row.
     *
     * @param playerNickname the player performing the action.
     * @param cardIndex the index of the card that the player wants to pick.
     */
    public void handleCardPickTopRow(String playerNickname, int cardIndex) {
        TurnManager turnManager = model.getTurnManager();

        try {
            Player activePlayer = turnManager.getActivePlayer();
            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
            }

            TribeCard card = model.getBoard().getTopCardFromIndex(cardIndex);

            CardVisitor cardPickerVisitor = new CardVisitor(){
              @Override
              public void visit(CharacterCard card){
                  turnManager.getPhase().pickCardFromTop(turnManager, activePlayer, card, model.getBoard());
              }
              @Override
              public void visit(EventCard card) throws IllegalGameActionException {
                  throw new IllegalGameActionException("Event cards cannot be picked!");
              }
            };

            card.accept(cardPickerVisitor);

        } catch (IllegalGameActionException | IndexOutOfBoundsException e) {
            System.err.println("Error for " + playerNickname + ": " + e.getMessage());
            // TODO: Inviare un pacchetto di Errore al Client
        }
    }

    /**
     * Handles the request of building picking from bottom row.
     *
     * @param playerNickname the player performing the action.
     * @param cardIndex the index of the building that the player wants to pick.
     */
    public void handleBuildingPickBottomRow(String playerNickname, int cardIndex) {
        TurnManager turnManager = model.getTurnManager();

        try {
            Player activePlayer = turnManager.getActivePlayer();

            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
            }

            BuildingCard card = model.getBoard().getBottomBuildingFromIndex(cardIndex);

            turnManager.getPhase().pickCardFromBottom(turnManager, activePlayer, card, model.getBoard());

        } catch (IllegalGameActionException | IndexOutOfBoundsException e) {
            System.err.println("Error for " + playerNickname + ": " + e.getMessage()); // TODO : communicate the error to the view
        }
    }

    /**
     * Handles the request of building picking from top row.
     *
     * @param playerNickname the player performing the action.
     * @param cardIndex the index of the building that the player wants to pick.
     */
    public void handleBuildingPickTopRow(String playerNickname, int cardIndex) {
        TurnManager turnManager = model.getTurnManager();

        try {
            Player activePlayer = turnManager.getActivePlayer();

            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
            }

            BuildingCard card = model.getBoard().getTopBuildingFromIndex(cardIndex);

            turnManager.getPhase().pickCardFromTop(turnManager, activePlayer, card, model.getBoard());

        } catch (IllegalGameActionException | IndexOutOfBoundsException e) {
            System.err.println("Error for " + playerNickname + ": " + e.getMessage()); // TODO : communicate the error to the view
        }
    }

}