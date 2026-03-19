package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.Player;

import java.util.ArrayList;
import java.util.List;

public class OfferResolutionPhase extends Phase implements DrawSubject {
    private final List<DrawObserver> observers = new ArrayList<>();

    public OfferResolutionPhase() {
    }

    /**
     * this method executes the offer resolution actions:
     * resolves the effect of each occupied slot on the offer track,
     * notifies building observers if the player meets special conditions
     * and reorganizes the turn order tile.
     *
     * @param turnManager the turn manager controlling the flow of the game.
     */
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
                for (TileSlot orderTile : turnManager.getTurnOrderTile().slots()) {
                    if (orderTile.getPlayer() == null) {
                        orderTile.setPlayer(oldPlayer);
                        break;
                    }
                }
            }
        }
    }

    /**
     * this method adds an observer to be notified during the card drawing process.
     *
     * @param observer the observer to add.
     */
    @Override
    public void addObserver(DrawObserver observer) {
        observers.add(observer);
    }

    /**
     * this method removes an observer from the notification list.
     *
     * @param observer the observer to remove.
     */
    @Override
    public void removeObserver(DrawObserver observer) {
        observers.remove(observer);
    }

    /**
     * this method notifies all registered observers that
     * a specific player has triggered an update.
     *
     * @param player the player who triggered the notification.
     */
    @Override
    public void notifyObserverBuildings(Player player) {
        for (DrawObserver observer : observers) {
            observer.update(player);
        }
    }


}
