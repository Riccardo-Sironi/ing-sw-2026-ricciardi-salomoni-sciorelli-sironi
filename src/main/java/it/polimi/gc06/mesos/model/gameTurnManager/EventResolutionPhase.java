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

    public void resolveEvent(TurnManager turnManager, Board board) throws IllegalPhaseActionException {

        ArrayList<EventCard> events = board.cleanBottomRow();

        events.forEach(card -> {
            turnManager.getPlayersOrder().forEach(card::resolveEvent);
        });

        turnManager.setPhase(new EndOfRoundPhase());
    }

}
