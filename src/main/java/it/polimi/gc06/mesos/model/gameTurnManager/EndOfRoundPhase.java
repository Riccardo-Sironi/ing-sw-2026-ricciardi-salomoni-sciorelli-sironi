package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;

import java.util.ArrayList;

public class EndOfRoundPhase extends Phase {

    private boolean isStarted;

    public EndOfRoundPhase(ModifierBuildingCard pickFromTopCard) {
        this.isStarted = false;
    }

    public void startEndOfRound(TurnManager turnManager, ArrayList<Player> players, TileSlot tileSlot) throws IllegalPhaseActionException {

        players.forEach((Player player) -> player.setTopDrawNum(player.getBuildingCards().contains(turnManager.getPickFromTopCard()) ? 1 : 0));
        isStarted = true;

    }

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

    private void checkIfPlayerIsFinished(TurnManager turnManager, Player player, Board board) throws IllegalPhaseActionException {

        if (!isStarted) {
            throw new IllegalPhaseActionException("You have to start the offer resolution phase first!");
        }

    }

    /**
     * this method executes the end of round actions:
     * resolves all event cards left in the bottom row for every player,
     * moves the top row cards to the bottom row and repopulate the top row
     *
     * @param turnManager the turn manager controlling the flow of the game.
     */
    public void endOfRound(TurnManager turnManager, Board board, GameModel gameModel) throws IllegalPhaseActionException {

        board.moveFromTopToBottom();

        // TODO Remove Game model? We need it to get the decks and stuff. Check Board
        board.populateTopRow(gameModel);

        turnManager.setRound(turnManager.getRound() + 1);

        // TODO Check whether game is over
        if (board.isEndGame()) {
            // TODO Go to EndGame Phase
            return;
        }

    }


}
