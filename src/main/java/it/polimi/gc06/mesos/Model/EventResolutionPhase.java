package it.polimi.gc06.mesos.Model;

public class EventResolutionPhase extends Phase {

    private final Board board;

    public EventResolutionPhase(Board board) {
        this.board = board;
    }

    public void resolveEvents(TurnManager turnManager) {
        for (TribeCard bottomCard : board.getBottomRow()) {
            bottomCard.accept(turnManager.getCardVisitor());
        }
    }

    public void placeTotems(TurnManager turnManager) {

    }

    public void resolveOffers(TurnManager turnManager) {

    }

    public void endOfRound(TurnManager turnManager, Player player) {

    }
}
