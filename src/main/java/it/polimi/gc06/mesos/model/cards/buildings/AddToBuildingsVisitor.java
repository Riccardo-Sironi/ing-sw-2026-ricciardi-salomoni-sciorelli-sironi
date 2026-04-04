package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;

public class AddToBuildingsVisitor extends CardVisitor {
    Player player;

    public AddToBuildingsVisitor(Player player) {
        this.player = player;
    }

    @Override
    public void visit(EndGameBuildingCard building) {
        player.addBuildingCards(building);
    }

    @Override
    public void visit(ModifierBuildingCard building) {
        player.addBuildingCards(building);
    }

    @Override
    public void visit(ObserverSetBuildingCard building) {
        player.addBuildingCards(building);
    }

    @Override
    public void visit(ObserverPairBuildingCard building) {
        player.addBuildingCards(building);
    }

    @Override
    public void visit(BuildingCard building) {
        throw new IllegalGameActionException("Test.");
    }
}
