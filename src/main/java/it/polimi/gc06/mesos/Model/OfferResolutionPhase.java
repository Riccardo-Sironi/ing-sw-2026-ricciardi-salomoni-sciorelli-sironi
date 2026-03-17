package it.polimi.gc06.mesos.Model;

import java.util.ArrayList;
import java.util.List;

public class OfferResolutionPhase extends Phase implements DrawSubject {
    private final List<DrawObserver> observers = new ArrayList<>();

    public OfferResolutionPhase() {
    }

    @Override
    public void action(TurnManager turnManager) {
        for (TileSlot tileSlot : turnManager.getOfferTrack()) {
            Player player = tileSlot.getPlayer();
            if (player != null) {
                // Either draw or add food tokens, depending on the tile
                tileSlot.applyEffect();

                // If the player owns one of the two buildings that update on draw
                // ( completed set or two inventors with the same icon), notify the observers
                if (player.hasSetBuildingCard() || player.hasPairBuildingCard()) {
                    notifyObserverBuildings(player);
                }


                // Add the player back on the turn order, place him on the order tile and remove him from the offer track
                turnManager.getPlayersOrder().addLast(player);
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
    public void addObserver(DrawObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(DrawObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObserverBuildings(Player player) {
        for (DrawObserver observer : observers) {
            observer.update(player);
        }
    }


}
