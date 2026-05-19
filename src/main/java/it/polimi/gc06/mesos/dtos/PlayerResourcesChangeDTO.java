package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class PlayerResourcesChangeDTO implements SmallModelEditor {

    private final String player;
    private final int topDrawNum;
    private final int bottomDrawNum;
    private final int food;
    private final int prestige;

    public PlayerResourcesChangeDTO(String player, int topDrawNum, int bottomDrawNum, int food, int prestige) {
        this.topDrawNum = topDrawNum;
        this.bottomDrawNum = bottomDrawNum;
        this.food = food;
        this.prestige = prestige;
        this.player = player;
    }

    @Override
    public void edit(SmallModel smallModel) {
        if (smallModel.getPlayer().getNickname().equals(player)) {
            if (food != 0) smallModel.getPlayer().setNumFood(food);
            if (prestige != 0)
                smallModel.getPlayer().setNumPrestige(prestige);
            if (bottomDrawNum != 0) smallModel.setBottomDrawNum(bottomDrawNum);
            if (topDrawNum != 0) smallModel.setTopDrawNum(topDrawNum);
        } else {
            PlayerView view = smallModel.getOpponents().stream().filter(v -> v.getNickname().equals(player))
                    .findFirst().orElseThrow(IllegalStateException::new);
            if (food != 0) view.setNumFood(food);
            if (prestige != 0) view.setNumPrestige(prestige);
        }
    }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }
}
