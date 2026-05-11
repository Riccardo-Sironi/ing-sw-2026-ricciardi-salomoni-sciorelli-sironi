package it.polimi.gc06.mesos.DTOs;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.util.ArrayList;
import java.util.Map;

public class GameStartedDTO implements SmallModelEditor{

    private final ArrayList<String> playersOrder;
    private final Map<String,Color> colorMap;
    private final Map<String, Integer> foodMap;
    private final ArrayList<Card> topRow;
    private final ArrayList<Card> topBuildings;

    public GameStartedDTO(ArrayList<String> playersOrder, Map<String, Color> colorMap, Map<String, Integer> foodMap, ArrayList<Card> topRow, ArrayList<Card> topBuildings) {
        this.playersOrder = playersOrder;
        this.colorMap = colorMap;
        this.foodMap = foodMap;
        this.topRow = topRow;
        this.topBuildings = topBuildings;
    }

    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException {
        smallModel.setEra(Era.ERA_I);
        smallModel.setPhase(new PlacingTotemPhase().toString());
        smallModel.setRound(0);
        smallModel.getTopBuildings().addAll(topBuildings);
        smallModel.getTopRow().addAll(topRow);
        for(String player : playersOrder){
            PlayerView pv = new PlayerView(player,colorMap.get(player));
            pv.setNumFood(foodMap.get(player));
            smallModel.getTurnOrderTile().add(pv);
        }
    }
}
