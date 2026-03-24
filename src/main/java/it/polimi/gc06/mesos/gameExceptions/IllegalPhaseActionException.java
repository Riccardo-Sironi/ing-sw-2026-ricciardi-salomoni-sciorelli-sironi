package it.polimi.gc06.mesos.gameExceptions;

/**
 * exception used only if the player does an action that is not permitted in that specific PHASE of the game.
 */
public class IllegalPhaseActionException extends IllegalGameActionException{
    public IllegalPhaseActionException(String message) {
        super(message);
    }
}
