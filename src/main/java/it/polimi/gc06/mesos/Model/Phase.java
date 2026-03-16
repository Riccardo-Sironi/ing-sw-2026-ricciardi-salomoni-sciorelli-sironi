package it.polimi.gc06.mesos.Model;

public abstract class Phase {
    
    public Phase() {
    }

    public abstract void placeTotems(TurnManager turnManager);

    public abstract void resolveOffers(TurnManager turnManager);

    public abstract void resolveEvents(TurnManager turnManager);

    public abstract void endOfRound(TurnManager turnManager, Player player);
}
