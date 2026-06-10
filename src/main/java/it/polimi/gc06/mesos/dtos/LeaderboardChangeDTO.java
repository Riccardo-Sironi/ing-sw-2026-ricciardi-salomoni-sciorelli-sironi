package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.network.leaderboard.Score;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.util.List;

/**
 * Delivers the latest ranking scores straight to the small model when prestige or ties have shifted.
 */
public class LeaderboardChangeDTO implements SmallModelEditor {

    private final List<Score> leaderboard;
    private Integer sequenceNumber;

    /**
     * @param leaderboard The new sorted list of players and their scores.
     */
    public LeaderboardChangeDTO(List<Score> leaderboard) {
        this.leaderboard = leaderboard;
        this.sequenceNumber = null;
    }

    /**
     * {@inheritDoc}
     *
     * @param smallModel the client's small model.
     * @throws IllegalStateException if state modifications fail.
     * @throws Error                 on deeper failure situations.
     */
    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException, Error {
        smallModel.setLeaderboard(leaderboard);
    }

    /**
     * {@inheritDoc}
     *
     * @param visitor
     */
    @Override
    public void accept(DTOVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void setSequenceNumber(int sNum) {
        sequenceNumber = sNum;
    }

    @Override
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }
}
