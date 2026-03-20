package it.polimi.gc06.mesos.model.cards.buildings;

//TODO per ora non usata, toglierla?
public interface BuildingCardVisitor {

    void visit(EndGameBuildingCard building);
    void visit(ModifierBuildingCard building);
    void visit(ObserverSetBuildingCard building);
    void visit(ObserverPairBuildingCard building);
    void visit(BuildingCard building);

}
