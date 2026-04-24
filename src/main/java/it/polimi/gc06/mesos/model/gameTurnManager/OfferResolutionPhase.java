package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;

public class OfferResolutionPhase extends Phase {

    private boolean isStarted = false;

    @Override
    public void startPlayerOfferResolution(TurnManager turnManager, Player player, TileSlot tileSlot) throws IllegalPhaseActionException {
        if (turnManager.getActivePlayer() != player) {
            throw new IllegalPhaseActionException("It's not your turn yet!");
        }
        if (tileSlot == null) {
            throw new IllegalArgumentException("Tile slot is null!");
        }

        tileSlot.applyEffect();

        isStarted = true;

        // TODO : if the player is in the offer tile 0, which gives food but doesn't allow to draw cards, we have to call
        // the checkIfPlayerFinishedMethod to let the player skip the offer resolution phase and move on with the next one

        if (player.getTopDrawNum() == 0 && player.getBottomDrawNum() == 0) {
            checkIfPlayerIsFinished(turnManager, player, turnManager.getGameModel().getBoard());
        }

        // TODO Notify Buildings on Draw
    }

    @Override
    public void pickCardFromTop(TurnManager turnManager, Player player, CharacterCard card, Board board) throws IllegalPhaseActionException {

        if (!isStarted) {
            throw new IllegalPhaseActionException("You have to start the offer resolution phase first!");
        }

        if (player.getTopDrawNum() <= 0) {
            throw new IllegalPhaseActionException("You can't draw from the top row anymore!");
        }

        board.pickCardFromTopRow(player, card);
        player.setTopDrawNum(player.getTopDrawNum() - 1);
        checkIfPlayerIsFinished(turnManager, player, board);
    }

    @Override
    public void pickCardFromBottom(TurnManager turnManager, Player player, CharacterCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException {

        if (!isStarted) {
            throw new IllegalPhaseActionException("You have to start the offer resolution phase first!");
        }

        if (player.getBottomDrawNum() <= 0) {
            throw new IllegalPhaseActionException("You can't draw from the bottom row anymore!");
        }

        board.pickCardFromBottomRow(player, card);
        player.setBottomDrawNum(player.getBottomDrawNum() - 1);
        checkIfPlayerIsFinished(turnManager, player, board);
    }

    @Override
    public void pickCardFromTop(TurnManager turnManager, Player player, BuildingCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException, IllegalGameActionException {

        if (!isStarted) {
            throw new IllegalPhaseActionException("You have to start the offer resolution phase first!");
        }

        if (player.getTopDrawNum() <= 0) {
            throw new IllegalPhaseActionException("You can't draw from the top row anymore!");
        }

        board.buyBuildingFromTopRow(player, card);
        player.setTopDrawNum(player.getTopDrawNum() - 1);
        checkIfPlayerIsFinished(turnManager, player, board);
    }

    @Override
    public void pickCardFromBottom(TurnManager turnManager, Player player, BuildingCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException, IllegalGameActionException {

        if (!isStarted) {
            throw new IllegalPhaseActionException("You have to start the offer resolution phase first!");
        }

        if (player.getBottomDrawNum() <= 0) {
            throw new IllegalPhaseActionException("You can't draw from the bottom row anymore!");
        }

        board.buyBuildingFromBottomRow(player, card);
        player.setBottomDrawNum(player.getBottomDrawNum() - 1);
        checkIfPlayerIsFinished(turnManager, player, board);
    }

    public void checkIfPlayerIsFinished(TurnManager turnManager, Player player, Board board) throws IllegalPhaseActionException {

        if (!isStarted) {
            throw new IllegalPhaseActionException("You have to start the offer resolution phase first!");
        }

        if (player.getBottomDrawNum() == 0 && player.getTopDrawNum() == 0) {
            TileSlot playerSlot = board.getOfferTrackPlayerSlot(player);
            playerSlot.removePlayer();

            turnManager.getPlayersOrder().remove(player);

            for (TileSlot orderTile : board.getTurnOrderTile().slots()) {
                if (orderTile.getPlayer() == null) {
                    orderTile.setPlayer(player);
                    break;
                }
            }

            if (!board.isOfferTrackEmpty()) {
                Player nextPlayer = null;

                for (TileSlot offerTrackTile : board.getOfferTrack()) {
                    if (offerTrackTile.getPlayer() != null) {
                        nextPlayer = offerTrackTile.getPlayer();
                        break;
                    }
                }

                turnManager.setPhase(new OfferResolutionPhase());
                turnManager.getPhase().startPlayerOfferResolution(turnManager, nextPlayer, board.getOfferTrackPlayerSlot(nextPlayer));
            }
        }


        // if the offer track is empty then we can move on with the next phase
        if (board.isOfferTrackEmpty()) {
            turnManager.setPhase(new EventResolutionPhase());

            turnManager.getPlayersOrder().clear();

            // refill the players order list with the new order of the players based on the turn order tile
            for (TileSlot orderTile : board.getTurnOrderTile().slots()) {
                if (orderTile.getPlayer() != null) {
                    turnManager.getPlayersOrder().addLast(orderTile.getPlayer());
                }
            }

            // we call the method, we don't wait for no request from no client
            turnManager.getPhase().resolveEvent(turnManager, board);
        }
    }
}
