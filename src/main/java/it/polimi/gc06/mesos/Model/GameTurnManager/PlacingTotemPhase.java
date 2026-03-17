package it.polimi.gc06.mesos.Model.GameTurnManager;

import it.polimi.gc06.mesos.Model.Player;

public class PlacingTotemPhase extends Phase {

    @Override
    public void action(TurnManager turnManager) {

        for (Player p : turnManager.getPlayersOrder()) {

            // TODO Workaround temporaneo: Mettiamo sempre nello slot corrispondente all'activePlayerIndex
            // TODO Mentre aspettiamo una vera e propria vestione dei player

            turnManager.getOfferTrack().get(turnManager.getActivePlayerIndex()).setPlayer(p);
            turnManager.setActivePlayerIndex(turnManager.getActivePlayerIndex() + 1);
            // Remove the player from the turn order, so that he won't be able to play again in this phase
            turnManager.getPlayersOrder().removeFirst();

        }

        // Turn has ended. Reset active player index and move to the next phase
        turnManager.setActivePlayerIndex(0);
        turnManager.setPhase(new OfferResolutionPhase());

    }

}
