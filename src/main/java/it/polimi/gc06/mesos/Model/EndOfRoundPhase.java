package it.polimi.gc06.mesos.Model;

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
