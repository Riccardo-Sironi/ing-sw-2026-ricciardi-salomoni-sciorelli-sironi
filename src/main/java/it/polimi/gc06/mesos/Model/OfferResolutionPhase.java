package it.polimi.gc06.mesos.Model;

public class OfferResolutionPhase extends Phase {
    public OfferResolutionPhase() {
    }

    @Override
    public void resolveOffers(TurnManager turnManager) {
        for (TileSlot tileSlot : turnManager.getOfferTrack()) {
            if (tileSlot.getPlayer() != null) {
                tileSlot.applyEffect();
                turnManager.getPlayersOrder().addLast(tileSlot.getPlayer());
                Player oldPlayer = tileSlot.removePlayer();
                for (TileSlot orderTile : turnManager.getTurnOrderTile().getSlots()) {
                    if (orderTile.getPlayer() == null) {
                        orderTile.setPlayer(oldPlayer);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void placeTotems(TurnManager turnManager) {

    }

    @Override
    public void resolveEvents(TurnManager turnManager) {

    }

    @Override
    public void endOfRound(TurnManager turnManager, Player player) {

    }
}
