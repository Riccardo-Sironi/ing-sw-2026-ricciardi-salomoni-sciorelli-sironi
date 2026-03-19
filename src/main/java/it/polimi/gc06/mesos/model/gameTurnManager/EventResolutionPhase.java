package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;

public class EventResolutionPhase extends Phase {

    private final Board board;

    public EventResolutionPhase(Board board) {
        this.board = board;
    }

    public void action(TurnManager turnManager) {
        for (TribeCard bottomCard : board.getBottomRow()) {
            bottomCard.accept(turnManager.getCardVisitor());
        }
    }


}
