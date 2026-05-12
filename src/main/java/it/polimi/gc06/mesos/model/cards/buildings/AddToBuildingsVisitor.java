package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;

public class AddToBuildingsVisitor extends CardVisitor {
    Player player;

    public AddToBuildingsVisitor(Player player) {
        this.player = player;
    }

    /**
     * this method visits an EndGameBuildingCard and adds it to the player's building cards.
     *
     * @param building the EndGameBuildingCard to be added.
     */
    @Override
    public void visit(EndGameBuildingCard building) {
        player.addBuildingCards(building);
    }

    /**
     * this method visits an EndGameBuildingCard and adds it to the player's building cards.
     *
     * @param building the ModifierBuildingCard to be added.
     */
    @Override
    public void visit(ModifierBuildingCard building) {
        player.addBuildingCards(building);
    }

    /**
     * this method visits an EndGameBuildingCard and adds it to the player's building cards.
     *
     * @param building the ObserverSetBuildingCard to be added..
     */
    @Override
    public void visit(ObserverSetBuildingCard building) {
        player.addBuildingCards(building);
    }


    /**
     * this method visits an EndGameBuildingCard and adds it to the player's building cards.
     *
     * @param building the ObserverPairBuildingCard to be added.
     */
    @Override
    public void visit(ObserverPairBuildingCard building) {
        player.addBuildingCards(building);
    }


    /**
     * this method is used as a fallback visit method for a generic or unknown BuildingCard.
     * throws an exception because only concrete, recognized building card types
     * should be added to a player's inventory.
     *
     * @param building the generic BuildingCard.
     * @throws IllegalGameActionException always, indicating an invalid card type.
     */
    @Override
    public void visit(BuildingCard building) {
        throw new IllegalGameActionException("Test.");
    }
}
