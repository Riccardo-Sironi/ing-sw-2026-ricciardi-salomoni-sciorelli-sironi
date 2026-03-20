package it.polimi.gc06.mesos.gameExceptions;

public class GameObjectNotFoundException extends IllegalGameActionException {
    public GameObjectNotFoundException(String message) {
        super(message);
    }
}
