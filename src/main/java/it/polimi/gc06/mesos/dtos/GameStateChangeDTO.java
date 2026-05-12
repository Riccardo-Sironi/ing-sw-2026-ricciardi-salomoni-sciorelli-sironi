package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.gameTurnManager.OfferResolutionPhase;
import it.polimi.gc06.mesos.network.leaderboard.Score;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.util.ArrayList;
import java.util.Collection;

public class GameStateChangeDTO implements SmallModelEditor{

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

    public GameStateChangeDTO(Collection<Score> scores){
        this.era = null;
        this.round = -1;
        this.phase = null;
        leaderboard = new ArrayList<>(scores);
    }

    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException {
        if(phase != null){
            if(smallModel.getPhase().equals(new OfferResolutionPhase().toString())){
                for(int i = smallModel.getOfferTrack().size()-1; i>=0; i++) {
                    smallModel.getTurnOrderTile().set(smallModel.getTurnOrderTile().size() - i - 1,
                            smallModel.getOfferTrack().get(i).getPlayer());
                    smallModel.getOfferTrack().get(i).removePlayer();
                }
            }
            smallModel.setPhase(phase);
        }
        if(era != null){
            //top/bottom row
            smallModel.getBottomRow().clear(); //empty bottom
            smallModel.getBottomRow().addAll(smallModel.getTopRow()); //copy top to bottom
            smallModel.getTopRow().clear(); //empty top
            //buildings
            smallModel.getBottomBuildings().clear();; //empty bottom
            smallModel.getBottomBuildings().addAll(smallModel.getTopBuildings()); //copy top to bottom
            smallModel.getTopBuildings().clear();; //empty top
            smallModel.setEra(era);
        }
        if(round >= 0) smallModel.setRound(round);
        if(leaderboard != null) smallModel.setLeaderboard(leaderboard);
    }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }
}
