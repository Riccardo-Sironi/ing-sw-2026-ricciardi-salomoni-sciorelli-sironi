package it.polimi.gc06.mesos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import it.polimi.gc06.mesos.network.socket.commands.Command;
import it.polimi.gc06.mesos.network.socket.infos.CardPickedInfo;
import it.polimi.gc06.mesos.network.socket.infos.TotemMovedInfo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class GameController {

    private final GameModel model;
    private final PropertyChangeSupport support;

    public GameController(GameModel model) {
        this.model = model;
        support = new PropertyChangeSupport(this);
    }

    public GameModel getModel() {
        return model;
    }

    /**
     * Handles the request of tile placement
     *
     * @param playerNickname the player performing the action.
     * @param tileIndex      the index of the tile where the player wants to be placed.
     */
    public void handleTotemOfferTilePlacement(String playerNickname, int tileIndex) {
        TurnManager turnManager = model.getTurnManager();
        try {
            Player activePlayer = turnManager.getActivePlayer();

            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("It is not " + playerNickname + " turn !");
            }

            List<TileSlot> offerTrack = model.getBoard().getOfferTrack();
            if (tileIndex >= offerTrack.size() || tileIndex < 0) {
                throw new IllegalPhaseActionException("Requested tile does not exist !");
            }
            TileSlot tile = offerTrack.get(tileIndex);

            if (tile.getPlayer() != null) {
                throw new IllegalPhaseActionException("The tile is already occupied !");
            }

            turnManager.getPhase().placeTotem(turnManager, activePlayer, tile, model.getBoard());

            TotemMovedInfo info = new TotemMovedInfo(playerNickname, tileIndex);
            support.firePropertyChange("totemMoved", null, info);

        } catch (IllegalPhaseActionException e) {
            System.err.println("Error for " + playerNickname + ": " + e.getMessage()); // TODO : communicate the error to the view
        }
    }

    /**
     * Handles the request of card picking from bottom row.
     *
     * @param playerNickname the player performing the action.
     * @param cardIndex      the index of the card that the player wants to pick.
     */
    public void handleCardPickBottomRow(String playerNickname, int cardIndex) {
        TurnManager turnManager = model.getTurnManager();

        try {
            Player activePlayer = turnManager.getActivePlayer();
            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
            }

            TribeCard card = model.getBoard().getBottomCardFromIndex(cardIndex);
            CardPickedInfo info = new CardPickedInfo(playerNickname, card); //prepares the info if needed

            CardVisitor cardPickerVisitor = new CardVisitor() {
                @Override
                public void visit(CharacterCard card) {
                    turnManager.getPhase().pickCardFromBottom(turnManager, activePlayer, card, model.getBoard());
                }

                @Override
                public void visit(EventCard card) throws IllegalGameActionException {
                    throw new IllegalGameActionException("Event cards cannot be picked!");
                }
            };

            card.accept(cardPickerVisitor);

            //if the operation was not successful the notice won't be sent
            support.firePropertyChange("cardPicked", null, info);
        } catch (IllegalGameActionException | IndexOutOfBoundsException e) {
            System.err.println("Error for " + playerNickname + ": " + e.getMessage());
            // TODO: Inviare un pacchetto di Errore al Client
        }
    }

    /**
     * Handles the request of card picking from top row.
     *
     * @param playerNickname the player performing the action.
     * @param cardIndex      the index of the card that the player wants to pick.
     */
    public void handleCardPickTopRow(String playerNickname, int cardIndex) {
        TurnManager turnManager = model.getTurnManager();

        try {
            Player activePlayer = turnManager.getActivePlayer();
            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
            }

            TribeCard card = model.getBoard().getTopCardFromIndex(cardIndex);
            CardPickedInfo info = new CardPickedInfo(playerNickname, card); //prepares the info if needed

            CardVisitor cardPickerVisitor = new CardVisitor() {
                @Override
                public void visit(CharacterCard card) {
                    turnManager.getPhase().pickCardFromTop(turnManager, activePlayer, card, model.getBoard());
                }

                @Override
                public void visit(EventCard card) throws IllegalGameActionException {
                    throw new IllegalGameActionException("Event cards cannot be picked!");
                }
            };

            card.accept(cardPickerVisitor);

            //if the operation was not successful the notice won't be sent
            support.firePropertyChange("cardPicked", null, info);
        } catch (IllegalGameActionException | IndexOutOfBoundsException e) {
            System.err.println("Error for " + playerNickname + ": " + e.getMessage());
            // TODO: Inviare un pacchetto di Errore al Client
        }
    }

    /**
     * Handles the request of building picking from bottom row.
     *
     * @param playerNickname the player performing the action.
     * @param cardIndex      the index of the building that the player wants to pick.
     */
    public void handleBuildingPickBottomRow(String playerNickname, int cardIndex) {
        TurnManager turnManager = model.getTurnManager();

        try {
            Player activePlayer = turnManager.getActivePlayer();

            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
            }

            BuildingCard card = model.getBoard().getBottomBuildingFromIndex(cardIndex);
            CardPickedInfo info = new CardPickedInfo(playerNickname, card); //prepares the info if needed

            turnManager.getPhase().pickCardFromBottom(turnManager, activePlayer, card, model.getBoard());

            //if the operation was not successful the notice won't be sent
            support.firePropertyChange("buildingPicked", null, info);

        } catch (IllegalGameActionException | IndexOutOfBoundsException e) {
            System.err.println("Error for " + playerNickname + ": " + e.getMessage()); // TODO : communicate the error to the view
        }
    }

    /**
     * Handles the request of building picking from top row.
     *
     * @param playerNickname the player performing the action.
     * @param cardIndex      the index of the building that the player wants to pick.
     */
    public void handleBuildingPickTopRow(String playerNickname, int cardIndex) {
        TurnManager turnManager = model.getTurnManager();

        try {
            Player activePlayer = turnManager.getActivePlayer();

            if (!activePlayer.getNickname().equals(playerNickname)) {
                throw new IllegalPhaseActionException("It's not " + playerNickname + " turn !");
            }

            BuildingCard card = model.getBoard().getTopBuildingFromIndex(cardIndex);
            CardPickedInfo info = new CardPickedInfo(playerNickname, card); //prepares the info if needed

            turnManager.getPhase().pickCardFromTop(turnManager, activePlayer, card, model.getBoard());

            //if the operation was not successful the notice won't be sent
            support.firePropertyChange("buildingPicked", null, info);

        } catch (IllegalGameActionException | IndexOutOfBoundsException e) {
            System.err.println("Error for " + playerNickname + ": " + e.getMessage()); // TODO : communicate the error to the view
        }
    }

    /**
     * Parses a command and executes it.
     * Example of valid JSON string: "{\"request\": \"TOP_CARD_REQUEST\", \"index\": 2, \"nickname\": \"Marco\"}"
     *
     * @param s the string that needs to be parsed to a command.
     * @throws JsonProcessingException if parsing goes wrong due to JSON errors.
     */
    public synchronized void parse(String s) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Command command = mapper.readValue(s, Command.class);
        command.execute(this);
    }

    public boolean isGameFinished() {
        return model.getBoard().isEndGame();
    }

    public void addListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

}