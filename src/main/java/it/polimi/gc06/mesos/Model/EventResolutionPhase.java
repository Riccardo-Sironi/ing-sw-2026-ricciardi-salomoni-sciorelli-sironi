package it.polimi.gc06.mesos.Model;

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
