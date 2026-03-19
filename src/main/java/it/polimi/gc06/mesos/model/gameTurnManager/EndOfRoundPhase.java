package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;

public class EndOfRoundPhase {

    private final Board board;

    /**
     * this method constructs the end of round phase with the specified game board.
     *
     * @param board the game board to interact with during this phase.
     */
    public EndOfRoundPhase(Board board) {
        this.board = board;
    }

    /**
     * this method executes the end of round actions:
     * resolves all event cards left in the bottom row for every player,
     * moves the top row cards to the bottom row and repopulate the top row
     *
     * @param turnManager the turn manager controlling the flow of the game.
     */
    public void action(TurnManager turnManager) {
        for (EventCard event : board.cleanBottomRow()) {
            for (Player player : turnManager.getPlayersOrder()) {
                event.resolveEvent(player);
            }
        }
        board.moveFromTopToBottom();
        // TODO Remove Context
        board.populateTopRow(new GameModel());
    }


}
