package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;

import java.util.ArrayList;
import java.util.List;

public class OfferResolutionPhase extends Phase implements DrawSubject {
    private final List<DrawObserver> observers = new ArrayList<>();

    public OfferResolutionPhase() {
    }

    @Override
    public void startPlayerOfferResolution(TurnManager turnManager, Player player, TileSlot tileSlot) throws IllegalPhaseActionException {

        if (turnManager.getNextPlayer() != player) {
            throw new IllegalPhaseActionException("It's not your turn yet!");
        }

        tileSlot.applyEffect();

        // If the player owns one of the two buildings that update on draw
        // ( completed set or two inventors with the same icon), notify the observers
        if (player.hasSetBuildingCard() || player.hasPairBuildingCard()) {
            notifyObserverBuildings(player);
        }
    }

    @Override
    public void pickCardFromBottom(TurnManager turnManager, Player player, CharacterCard card) throws IllegalPhaseActionException {
        if (turnManager.getNextPlayer() != player) {
            throw new IllegalPhaseActionException("It's not your turn yet!");
        }
        if (player.getBottomDrawNum() <= 0) {
            throw new IllegalPhaseActionException("You can't draw from the bottom row anymore!");
        }

        player.setBottomDrawNum(player.getBottomDrawNum() - 1);
        checkIfPlayerIsFinished(turnManager, player);
    }

    @Override
    public void pickCardFromTop(TurnManager turnManager, Player player, CharacterCard card) throws IllegalPhaseActionException {
        if (turnManager.getNextPlayer() != player) {
            throw new IllegalPhaseActionException("It's not your turn yet!");
        }

        if (player.getTopDrawNum() <= 0) {
            throw new IllegalPhaseActionException("You can't draw from the top row anymore!");
        }

        player.setTopDrawNum(player.getTopDrawNum() - 1);
    }

    private void checkIfPlayerIsFinished(TurnManager turnManager, Player player) throws IllegalPhaseActionException {
        if (player.getBottomDrawNum() == 0 && player.getTopDrawNum() == 0) {
            TileSlot playerSlot = turnManager.getOfferTrack().getPlayerSlot(player);
            playerSlot.removePlayer();
        }

        for (TileSlot orderTile : turnManager.getTurnOrderTile().slots()) {
            if (orderTile.getPlayer() == null) {
                orderTile.setPlayer(player);
                break;
            }
        }

        turnManager.advanceResolutionTurn();
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
