package it.polimi.gc06.mesos.Model.GameTurnManager;

import it.polimi.gc06.mesos.Model.Cards.Events.EventCard;
import it.polimi.gc06.mesos.Model.GameBoard.Board;
import it.polimi.gc06.mesos.Model.GameModel;
import it.polimi.gc06.mesos.Model.Player;

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
