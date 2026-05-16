package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.gameBoard.TileEffect;
import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import it.polimi.gc06.mesos.view.smallModel.TileSlotView;

import java.util.ArrayList;
import java.util.Map;

public class GameStartedDTO implements SmallModelEditor{

    private final ArrayList<String> playersOrder;
    private final Map<String,Color> colorMap;
    private final Map<String, Integer> foodMap;
    private final ArrayList<Card> topRow;
    private final ArrayList<Card> topBuildings;
    private final String nickname;
    private final boolean isActive;
    private final ArrayList<TileEffect> tileEffects;

    public GameStartedDTO(String nickname, ArrayList<String> playersOrder, Map<String, Color> colorMap,
                          Map<String, Integer> foodMap, ArrayList<Card> topRow, ArrayList<Card> topBuildings, boolean isActive,
                          ArrayList<TileEffect> tileEffects) {
        this.playersOrder = playersOrder;
        this.colorMap = colorMap;
        this.foodMap = foodMap;
        this.topRow = topRow;
        this.topBuildings = topBuildings;
        this.nickname = nickname;
        this.isActive = isActive;
        this.tileEffects = tileEffects;
    }

    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException {

        //state
        smallModel.setEra(Era.ERA_I);
        smallModel.setPhase(new PlacingTotemPhase().toString());
        smallModel.setRound(0);

        //structure
        smallModel.getTopBuildings().addAll(topBuildings);
        smallModel.getTopRow().addAll(topRow);
        tileEffects.forEach(e -> smallModel.getOfferTrack().add(new TileSlotView(e)));

        //player
        smallModel.setPlayer(nickname,colorMap.get(nickname));
        smallModel.setActive(isActive);
        for(String player : playersOrder){
            PlayerView pv = new PlayerView(player,colorMap.get(player));
            pv.setNumFood(foodMap.get(player));
            smallModel.getTurnOrderTile().add(pv);
            if(!pv.getNickname().equals(nickname)) smallModel.addOpponent(pv);
        }
    }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }
}
