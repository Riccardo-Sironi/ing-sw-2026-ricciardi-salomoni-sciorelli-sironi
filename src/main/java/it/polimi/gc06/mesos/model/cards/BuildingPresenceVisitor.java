package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.cards.buildings.*;

public class BuildingPresenceVisitor extends CardVisitor {
    boolean areThereBuildings = false;

    public BuildingPresenceVisitor() {
    }

    /**
     * This method returns whether any building card has been visited by this visitor.
     * NEVER USED.
     *
     * @return if at least one building card was found.
     */
    public boolean areThereBuildings() {
        return areThereBuildings;
    }

    /**
     * This method visits an ObserverPairBuildingCard and registers the presence of a building.
     *
     * @param building the ObserverPairBuildingCard to be visited.
     */
    @Override
    public void visit(ObserverPairBuildingCard building) {
        areThereBuildings = true;
    }

    /**
     * This method visits an ObserverSetBuildingCard and registers the presence of a building.
     *
     * @param building the ObserverSetBuildingCard to be visited.
     */
    @Override
    public void visit(ObserverSetBuildingCard building) {
        areThereBuildings = true;
    }

    /**
     * This method visits an ModifierBuildingCard and registers the presence of a building.
     *
     * @param building the ModifierBuildingCard to be visited.
     */
    @Override
    public void visit(ModifierBuildingCard building) {
        areThereBuildings = true;
    }

    /**
     * This method visits an EndGameBuildingCard and registers the presence of a building.
     *
     * @param building the EndGameBuildingCard to be visited.
     */
    @Override
    public void visit(EndGameBuildingCard building) {
        areThereBuildings = true;
    }

    /**
     * This method visits a generic BuildingCard and registers the presence of a building.
     *
     * @param card the BuildingCard to be visited.
     */
    @Override
    public void visit(BuildingCard card) {
        areThereBuildings = true;
    }
}
