package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.cards.buildings.*;

public class BuildingPresenceVisitor extends CardVisitor {
    boolean areThereBuildings = false;

    public BuildingPresenceVisitor() {
    }

    public boolean areThereBuildings() {
        return areThereBuildings;
    }

    @Override
    public void visit(ObserverPairBuildingCard building) {
        areThereBuildings = true;
    }

    @Override
    public void visit(ObserverSetBuildingCard building) {
        areThereBuildings = true;
    }

    @Override
    public void visit(ModifierBuildingCard building) {
        areThereBuildings = true;
    }

    @Override
    public void visit(EndGameBuildingCard building) {
        areThereBuildings = true;
    }

    @Override
    public void visit(BuildingCard card) {
        areThereBuildings = true;
    }
}
