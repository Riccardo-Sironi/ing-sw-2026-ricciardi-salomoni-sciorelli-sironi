package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;

public class EndOfRoundPhase {

    private final Board board;

    public EndOfRoundPhase(Board board) {
        this.board = board;
    }

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
