package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;


public class PlacingTotemPhase extends Phase {
    /**
     * this method is used to execute the totem placement actions.
     * it iterates through the players and assigns them to an offer track slot
     * and transitions the game to the offer resolution phase.
     *
     * @param turnManager the turn manager orchestrating the game flow.
     */
    @Override
    public void placeTotem(TurnManager turnManager, Player player, TileSlot slot) throws IllegalPhaseActionException {

        if (!slot.isEmpty()) {
            throw new IllegalPhaseActionException("The slot is not empty!");
        }

        // Remove the player from the turn order
        turnManager.getPlayersOrder().removeFirst();

        // TODO Come vogliamo gestire i totem? Appartengono ai player? SONO i player stessi?
        slot.setPlayer(player);
    }

}
