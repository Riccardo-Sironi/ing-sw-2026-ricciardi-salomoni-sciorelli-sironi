package it.polimi.gc06.mesos.model.gameTurnManager;

public abstract class Phase {

    public Phase() {
    }


    // TODO Valutare se unire in singolo metodo
    public abstract void action(TurnManager turnManager);

}
