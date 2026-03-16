package it.polimi.gc06.mesos.Model;

public class PlacingTotemPhase extends Phase {

    public PlacingTotemPhase() {
    }

    @Override
    public void placeTotems(TurnManager turnManager) {

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

    @Override
    public void resolveOffers(TurnManager turnManager) {

    }

    @Override
    public void resolveEvents(TurnManager turnManager) {

    }

    @Override
    public void endOfRound(TurnManager turnManager, Player player) {

    }
}
