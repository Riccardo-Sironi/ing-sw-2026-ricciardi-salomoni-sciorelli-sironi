package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;

public abstract class Phase {

    public Phase() {
    }

    /**
     * this method executes the core logic and actions associated with this phase.
     *
     * @param turnManager the turn manager orchestrating the game flow.
     */
    public void placeTotem(TurnManager turnManager, Player player, TileSlot tileSlot) throws IllegalPhaseActionException {
        throw new IllegalPhaseActionException("You can't place a totem in this phase!");
    }

    public void resolveOffer(TurnManager turnManager, Player player) throws IllegalPhaseActionException {
        throw new IllegalPhaseActionException("You can't resolve an offer in this phase!");
    }

    public void resolveEvent(TurnManager turnManager, Player player) throws IllegalPhaseActionException {
        throw new IllegalPhaseActionException("You can't resolve an event in this phase!");
    }

    public void endOfRound(TurnManager turnManager) throws IllegalPhaseActionException {
        throw new IllegalPhaseActionException("You can't end the round in this phase!");
    }

}
