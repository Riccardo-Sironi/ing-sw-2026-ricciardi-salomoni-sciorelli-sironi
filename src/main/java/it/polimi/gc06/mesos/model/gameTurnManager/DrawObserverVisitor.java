package it.polimi.gc06.mesos.model.gameTurnManager;

public class DrawObserverVisitor implements PhaseVisitor{

    private DrawObserver observer;

    /**
     * Sets the observer that will be subscribed to the offerResolutionPhase observers list.
     *
     * @param observer the observer.
     */
    public void setObserver(DrawObserver observer) {
        this.observer = observer;
    }

    /**
     * Does nothing.
     *
     * @param phase the current GamePhase
     */
    @Override
    public void visit(PlacingTotemPhase phase) {

    }

    /**
     * Does nothing.
     *
     * @param phase the current GamePhase
     */
    @Override
    public void visit(EndOfRoundPhase phase) {

    }

    /**
     * Does nothing.
     *
     * @param phase the current GamePhase
     */
    @Override
    public void visit(EventResolutionPhase phase) {

    }

    /**
     * Adds the observer to the OfferResolutionPhase the observer needs to be specified
     * before the call using the setter setObserver(DrawObserver).
     *
     * @param phase the current GamePhase
     */
    @Override
    public void visit(OfferResolutionPhase phase) {
        phase.addObserver(observer);
    }

    /**
     * Default case.
     *
     * @param phase the current GamePhase
    */
    @Override
    public void visit(Phase phase) {

    }
}
