package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class PlayerResourcesChangeDTO implements SmallModelEditor {

    private final String player;
    private final Integer topDrawNum;
    private final Integer bottomDrawNum;
    private final Integer food;
    private final Integer prestige;

    public PlayerResourcesChangeDTO(String player, Integer topDrawNum, Integer bottomDrawNum, Integer food, Integer prestige) {
        this.topDrawNum = topDrawNum;
        this.bottomDrawNum = bottomDrawNum;
        this.food = food;
        this.prestige = prestige;
        this.player = player;
    }

    @Override
    public void edit(SmallModel smallModel) {
        if (smallModel.getPlayer().getNickname().equals(player)) {
            if (food != null) smallModel.getPlayer().setNumFood(food);
            if (prestige != null) smallModel.getPlayer().setNumPrestige(prestige);
            if (bottomDrawNum != null) smallModel.setBottomDrawNum(bottomDrawNum);
            if (topDrawNum != null) smallModel.setTopDrawNum(topDrawNum);
        } else {
            PlayerView view = smallModel.getOpponents().stream().filter(v -> v.getNickname().equals(player))
                    .findFirst().orElseThrow(IllegalStateException::new);
            if (food != null) view.setNumFood(food);
            if (prestige != null) view.setNumPrestige(prestige);
        }
    }

    public String getPlayer() {
        return player;
    }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }
}