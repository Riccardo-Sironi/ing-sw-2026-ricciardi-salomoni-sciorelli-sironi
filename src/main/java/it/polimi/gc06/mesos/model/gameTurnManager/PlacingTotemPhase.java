package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.dtos.PhaseChangeDTO;
import it.polimi.gc06.mesos.dtos.PlayerStateChangeDTO;
import it.polimi.gc06.mesos.dtos.TotemOfferMoveDTO;
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

        if (!turnManager.getPlayersOrder().isEmpty()) {
            //Notifies next player only if the list is not empty
            Player active = turnManager.getActivePlayer();
            PlayerStateChangeDTO dto3 = new PlayerStateChangeDTO(active.getNickname());
            dto3.setIsActive(true);
            turnManager.getNotifier().notifyChange(dto3);
        }

        for (TileSlot s : board.getTurnOrderTile().slots()) {
            if (s.getPlayer() != null && s.getPlayer().equals(player)) {
                s.removePlayer();
                break;
            }
        }

        slot.setPlayer(player);

        turnManager.getNotifier().notifyChange(new TotemOfferMoveDTO(player.getNickname(),
                board.getOfferTrack().indexOf(slot)));

        //If there are no more player we pass to the next phase
        if (turnManager.getPlayersOrder().isEmpty()) {
            turnManager.getNotifier().notifyChange(new PhaseChangeDTO(new OfferResolutionPhase().toString()));
            turnManager.setPhase(new OfferResolutionPhase());

            for (TileSlot s : board.getOfferTrack()) {
                if (s.getPlayer() != null) {
                    turnManager.getPlayersOrder().addLast(s.getPlayer());
                }
            }

            Player p = board.getOfferTrack().stream().filter(tile -> tile.getPlayer() != null).findFirst().map(TileSlot::getPlayer).orElse(null);
            if (p == null) throw new IllegalStateException("test");
            turnManager.getPhase().startPlayerOfferResolution(turnManager, p, board.getOfferTrackPlayerSlot(p));
        }
    }

    /**
     * This method returns the string representation of this phase.
     *
     * @return the string "placing_totem".
     */
    @Override
    public String toString() {
        return "placing_totem";
    }
}
