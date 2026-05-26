package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;

import java.util.ArrayList;

public class EventResolutionPhase extends Phase {

    /**
     * this method constructs the event resolution phase with the specified game board.
     *
     */
    public EventResolutionPhase() {
    }

    /**
     * This method resolves all the event cards currently present in the bottom row of the board.
     * The events are collected and cleared from the board, and each event's effect is
     * applied to all players according to the current turn order. After resolving
     * the events, the game transitions to the {@link EndOfRoundPhase} and triggers
     * the end-of-round routine.
     *
     * @param turnManager the turn manager controlling the flow of the game and the players' turn order.
     * @param board the game board from which the event cards are retrieved and removed.
     * @throws IllegalPhaseActionException if an invalid action occurs during the phase transition or the end-of-round execution.
     */
    @Override
    public void resolveEvent(TurnManager turnManager, Board board) throws IllegalPhaseActionException {

        ArrayList<EventCard> events = board.cleanBottomRow();

        events.forEach(card -> {
            turnManager.getPlayersOrder().forEach(card::resolveEvent);
        });

        turnManager.setPhase(new EndOfRoundPhase());
        turnManager.getPhase().endOfRound(turnManager, board, turnManager.getGameModel());
    }

    /**
     * This method returns the string representation of this phase.
     *
     * @return the string "event_resolution".
     */
    @Override
    public String toString() {
        return "event_resolution";
    }
}
