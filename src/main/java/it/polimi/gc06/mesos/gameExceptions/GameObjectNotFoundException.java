package it.polimi.gc06.mesos.gameExceptions;

/**
 * exception used only if a game's OBJECT not currently available is requested.
 */
public class GameObjectNotFoundException extends IllegalGameActionException {
    public GameObjectNotFoundException(String message) {
        super(message);
    }
}
