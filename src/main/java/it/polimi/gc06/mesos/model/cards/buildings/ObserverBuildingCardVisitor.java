package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.gameExceptions.GameObjectNotFoundException;
import it.polimi.gc06.mesos.model.GameInfo;
import it.polimi.gc06.mesos.model.gameTurnManager.DrawObserverVisitor;
import it.polimi.gc06.mesos.model.gameTurnManager.Phase;

//TODO per ora non usata, toglierla?
public class ObserverBuildingCardVisitor implements BuildingCardVisitor{


    private final GameInfo gameInfo;
    private final DrawObserverVisitor drawObserverVisitor;

    public ObserverBuildingCardVisitor(GameInfo gameInfo){
        this.gameInfo = gameInfo;
        drawObserverVisitor = new DrawObserverVisitor();
    }

    /**
     * This method does nothing, but it allows you to call the `visit()` methods on
     * `ObserverSetBuildingCard` and `ObserverPairBuildingCard`
     * without checking the card type.
     *
     * @param building the building that will be visited.
     */
    @Override
    public void visit(EndGameBuildingCard building) {}

    /**
     * This method does nothing, but it allows you to call the `visit()` methods on
     * `ObserverSetBuildingCard` and `ObserverPairBuildingCard`
     * without checking the card type.
     *
     * @param building the building that will be visited.
     */
    @Override
    public void visit(ModifierBuildingCard building) {}

    /**
     * This method subscribes the ObserverBuildingCard to the DrawSubject
     * taken from the constructors param.
     * @param building the building that will be visited.
     */
    //TODO does this conflict with phase management, is it possible for the phase to change before i can add the observer
    @Override
    public void visit(ObserverSetBuildingCard building) {
        Phase currentPhase;
        try {
            currentPhase = gameInfo.getCurrentPhase();
            drawObserverVisitor.visit(currentPhase);
        }catch (GameObjectNotFoundException _){}
    }

    /**
     * This method subscribes the ObserverBuildingCard to the DrawSubject
     * taken from the constructors param.
     * @param building the building that will be visited.
     */
    @Override
    public void visit(ObserverPairBuildingCard building) {

    }

    @Override
    public void visit(BuildingCard building) {

    }
}
