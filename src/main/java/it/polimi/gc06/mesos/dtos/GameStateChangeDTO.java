package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.gameTurnManager.OfferResolutionPhase;
import it.polimi.gc06.mesos.network.leaderboard.Score;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import it.polimi.gc06.mesos.view.smallModel.TileSlotView;

import java.util.ArrayList;
import java.util.Collection;

public class GameStateChangeDTO implements SmallModelEditor {

    private final Era era;
    private final int round;
    private final String phase;
    private final ArrayList<Score> leaderboard;

    public GameStateChangeDTO(Era era) {
        this.era = era;
        this.round = -1;
        this.phase = null;
        leaderboard = null;
    }

    public GameStateChangeDTO(String phase) {
        this.era = null;
        this.round = -1;
        this.phase = phase;
        leaderboard = null;
    }

    public GameStateChangeDTO(int round) {
        this.era = null;
        this.round = round;
        this.phase = null;
        leaderboard = null;
    }

    public GameStateChangeDTO(Collection<Score> scores) {
        this.era = null;
        this.round = -1;
        this.phase = null;
        leaderboard = new ArrayList<>(scores);
    }

    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException {
        // TODO : should separate the events

        if (phase != null) {
            if (smallModel.getPhase().equals(new OfferResolutionPhase().toString())) {
                int turnOrderCount = 0;
                for (TileSlotView tileSlot : smallModel.getOfferTrack()) {
                    if (tileSlot.getPlayer() != null) {
                        smallModel.getTurnOrderTile().set(turnOrderCount, tileSlot.getPlayer());
                        smallModel.getOfferTrack().get(smallModel.getOfferTrack().indexOf(tileSlot)).removePlayer();
                        turnOrderCount++;
                    }
                }
            }
            smallModel.setPhase(phase);
        }
        if (era != null) {
            //top/bottom row
            smallModel.getBottomRow().clear(); //empty bottom
            smallModel.getBottomRow().addAll(smallModel.getTopRow()); //copy top to bottom
            smallModel.getTopRow().clear(); //empty top
            //buildings
            smallModel.getBottomBuildings().clear();
            //empty bottom
            smallModel.getBottomBuildings().addAll(smallModel.getTopBuildings()); //copy top to bottom
            smallModel.getTopBuildings().clear();
            //empty top
            smallModel.setEra(era);
        }
        if (round >= 0) smallModel.setRound(round);
        if (leaderboard != null) smallModel.setLeaderboard(leaderboard);
    }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }

    public boolean isEndgame() {
        return leaderboard != null;
    }
}
