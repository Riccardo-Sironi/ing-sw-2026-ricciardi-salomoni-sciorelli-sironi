package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;

public abstract class Phase {

    public Phase() {
    }

    /**
     * this method executes the core logic and actions associated with this phase.
     *
     * @param turnManager the turn manager orchestrating the game flow.
     * @param board       the game board.
     */
    public void placeTotem(TurnManager turnManager, Player player, TileSlot tileSlot, Board board) throws IllegalPhaseActionException {
        throw new IllegalPhaseActionException("You can't place a totem in this phase!");
    }

    public void startPlayerOfferResolution(TurnManager turnManager, Player player, TileSlot tileSlot) throws IllegalPhaseActionException {
        throw new IllegalPhaseActionException("You can't resolve an offer in this phase!");
    }

    public void resolveEvent(TurnManager turnManager, Board board) throws IllegalPhaseActionException {
        throw new IllegalPhaseActionException("You can't resolve an event in this phase!");
    }

    public void endOfRound(TurnManager turnManager, Board board, GameModel gameModel) throws IllegalPhaseActionException {
        throw new IllegalPhaseActionException("You can't end the round in this phase!");
    }

    public void pickCardFromTop(TurnManager turnManager, Player player, CharacterCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException {
        throw new IllegalPhaseActionException("You cannot draw yet!");
    }

    public void pickCardFromBottom(TurnManager turnManager, Player player, CharacterCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException {
        throw new IllegalPhaseActionException("You cannot draw yet!");
    }

    public void pickCardFromTop(TurnManager turnManager, Player player, BuildingCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException {
        throw new IllegalPhaseActionException("You cannot draw yet!");
    }

    public void pickCardFromBottom(TurnManager turnManager, Player player, BuildingCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException {
        throw new IllegalPhaseActionException("You cannot draw yet!");
    }

    public void pickCardFromBottom(TurnManager turnManager, Player player, EventCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException {
        throw new IllegalPhaseActionException("You can't pick Event Cards during the Offer Resolution Phase!");
    }

    public void pickCardFromTop(TurnManager turnManager, Player player, EventCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException {
        throw new IllegalPhaseActionException("You can't pick Event Cards during the Offer Resolution Phase!");
    }

    public void skipPick(TurnManager turnManager) throws IllegalPhaseActionException {
        throw new IllegalPhaseActionException("You can't skip top pick in this phase!");
    }
}
