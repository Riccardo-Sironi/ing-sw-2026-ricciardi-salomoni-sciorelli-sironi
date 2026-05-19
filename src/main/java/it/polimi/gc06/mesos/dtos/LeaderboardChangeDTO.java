package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.network.leaderboard.Score;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.util.List;

public class LeaderboardChangeDTO implements SmallModelEditor {

    private final List<Score> leaderboard;

    public LeaderboardChangeDTO(List<Score> leaderboard) {
        this.leaderboard = leaderboard;
    }

    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException, Error {
        smallModel.setLeaderboard(leaderboard);
    }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }
}
