package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.gameBoard.Board;
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
    public void placeTotem(TurnManager turnManager, Player player, TileSlot slot, Board board) throws IllegalPhaseActionException {

        if (!slot.isEmpty()) {
            throw new IllegalPhaseActionException("The slot is not empty!");
        }

        turnManager.getPlayersOrder().removeFirst();

        for (TileSlot s : board.getTurnOrderTile().slots()) {
            if (s.getPlayer() != null) {
                s.setPlayer(null);
                break;
            }
        }

        slot.setPlayer(player);

        // if there are no player left on the turnOrderTile we move on with the next phase
        // TODO : we are duplicating the list of players (basically the playersOrder list is the turnOrderTile list)
        if (turnManager.getPlayersOrder().isEmpty()) {
            turnManager.setPhase(new OfferResolutionPhase());
        }
    }

}
