package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CharactersPresenceVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import javafx.scene.input.ScrollEvent;

public class OfferResolutionPhase extends Phase {

    private boolean isStarted = false;

    // TODO : la possibilità di skippare la fase la si da solo se non ci sono carte character da pescare VVVVV
    // TODO : deve essere differenziarta fra top e bottom VVVV
    // TODO : deve essere ricontrollato ogni volta che si pesca una carta VVVV
    // TODO : capire poi come si capiscese siamo nel pecaggio top o bottom

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

        checkIfPlayerIsFinished(turnManager, player, turnManager.getGameModel().getBoard());

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

    private boolean checkForRightToSkipTop(Player player, Board board) throws IllegalPhaseActionException {
        if (player.getTopDrawNum() > 0) {
            CharactersPresenceVisitor charactersPresenceVisitor = new CharactersPresenceVisitor();
            board.getTopRow().forEach(card -> card.accept(charactersPresenceVisitor));
            return !charactersPresenceVisitor.areCharactersPresent();
        } else {
            return true;
        }
    }

    private boolean checkForRightToSkipBottom(Player player, Board board) throws IllegalPhaseActionException {
        if (player.getBottomDrawNum() > 0) {
            CharactersPresenceVisitor charactersPresenceVisitor = new CharactersPresenceVisitor();
            board.getBottomRow().forEach(card -> card.accept(charactersPresenceVisitor));
            return !charactersPresenceVisitor.areCharactersPresent();
        } else {
            return true;
        }
    }

    /**
     * This method allows to skip the player picking phase if needed. An example is if there are no characters card to
     * pick or if the rows are empty.
     *
     * @param turnManager the model turn manager reference.
     * @throws IllegalPhaseActionException if the player can't skip the phase or if the offer resolution phase for that player hasn't started yet.
     */
    @Override
    public void skipPick(TurnManager turnManager) throws IllegalPhaseActionException {
        if (!isStarted) throw new IllegalPhaseActionException("You have to start the offer resolution phase first!");
        Player currentPlayer = turnManager.getActivePlayer();

        if (checkForRightToSkipTop(currentPlayer, turnManager.getGameModel().getBoard()) && checkForRightToSkipBottom(currentPlayer, turnManager.getGameModel().getBoard())) {
            currentPlayer.setTopDrawNum(0);
            currentPlayer.setBottomDrawNum(0);

            checkForRightToSkipTop(currentPlayer, turnManager.getGameModel().getBoard());
        } else {
            throw new IllegalPhaseActionException("You can't skip the offer resolution phase!");
        }
    }
}
