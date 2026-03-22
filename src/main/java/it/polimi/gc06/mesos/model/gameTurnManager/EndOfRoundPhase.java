package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.gameBoard.Board;

public class EndOfRoundPhase extends Phase {

    public EndOfRoundPhase() {
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
