package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.events.EventCard;

public class EventResolutionPhase extends Phase {

    // private final Board board;
    private final GameModel gameModel;
    private final EventCard currentEvent;

    /**
     * this method constructs the event resolution phase with the specified game board.
     *
     * @param currentEvent the current event card being resolved. This is needed to check for the event's effects and to apply them to the players and the game state.
     */
    public EventResolutionPhase(EventCard currentEvent) {

        // TODO Change
        this.gameModel = new GameModel();

        this.currentEvent = currentEvent;
    }

    public void resolveEvent(TurnManager turnManager) throws IllegalPhaseActionException {
        for (Player player : gameModel.getPlayers()) {
            resolveEvent(turnManager);
        }

        turnManager.setPhase(new EndOfRoundPhase());
    }

}
