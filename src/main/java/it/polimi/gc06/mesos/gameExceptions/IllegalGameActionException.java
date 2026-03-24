package it.polimi.gc06.mesos.gameExceptions;

/**
 * GENERAL game's exception.
 */
public class IllegalGameActionException extends RuntimeException {
    public IllegalGameActionException(String message) {
        super(message);
    }
}
