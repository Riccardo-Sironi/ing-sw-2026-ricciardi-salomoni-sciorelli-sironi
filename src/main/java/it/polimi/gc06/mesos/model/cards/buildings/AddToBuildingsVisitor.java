package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;

/**
 * A visitor responsible for adding specific building card types to a player's inventory.
 * It implements a fallback that rejects unrecognized or generic building cards.
 */
public class AddToBuildingsVisitor extends CardVisitor {
    Player player;

    /**
     * Constructs a new AddToBuildingsVisitor for the specified player.
     *
     * @param player The player who will receive the building cards.
     */
    public AddToBuildingsVisitor(Player player) {
        this.player = player;
    }

    /**
     * This method visits an EndGameBuildingCard and adds it to the player's building cards.
     *
     * @param building The EndGameBuildingCard to be added.
     */
    @Override
    public void visit(EndGameBuildingCard building) {
        player.addBuildingCards(building);
    }

    /**
     * This method visits a ModifierBuildingCard and adds it to the player's building cards.
     *
     * @param building The ModifierBuildingCard to be added.
     */
    @Override
    public void visit(ModifierBuildingCard building) {
        player.addBuildingCards(building);
    }

    /**
     * This method visits an ObserverSetBuildingCard and adds it to the player's building cards.
     *
     * @param building The ObserverSetBuildingCard to be added.
     */
    @Override
    public void visit(ObserverSetBuildingCard building) {
        player.addBuildingCards(building);
    }

    /**
     * This method visits an ObserverPairBuildingCard and adds it to the player's building cards.
     *
     * @param building The ObserverPairBuildingCard to be added.
     */
    @Override
    public void visit(ObserverPairBuildingCard building) {
        player.addBuildingCards(building);
    }

    /**
     * This method is used as a fallback visit method for a generic or unknown BuildingCard.
     * Throws an exception because only concrete, recognized building card types
     * should be added to a player's inventory.
     *
     * @param building The generic BuildingCard.
     * @throws IllegalGameActionException Always, indicating an invalid card type.
     */
    @Override
    public void visit(BuildingCard building) {
        throw new IllegalGameActionException("Test.");
    }
}