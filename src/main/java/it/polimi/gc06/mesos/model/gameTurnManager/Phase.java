package it.polimi.gc06.mesos.model.gameTurnManager;

public abstract class Phase {

    public Phase() {
    }


    // TODO Valutare se unire in singolo metodo
    /**
     * this method executes the core logic and actions associated with this phase.
     *
     * @param turnManager the turn manager orchestrating the game flow.
     */
    public abstract void action(TurnManager turnManager);

}
