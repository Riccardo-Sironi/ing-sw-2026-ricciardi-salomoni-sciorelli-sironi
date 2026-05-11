package it.polimi.gc06.mesos.DTOs;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class PlayerResourcesChangeDTO implements SmallModelEditor{

    private final String player;
    private final int topDrawNumDelta;
    private final int bottomDrawNumDelta;
    private final int foodDelta;
    private final int prestigeDelta;

    public PlayerResourcesChangeDTO(String player, int topDrawNumDelta, int bottomDrawNumDelta, int foodDelta, int prestigeDelta) {
        this.topDrawNumDelta = topDrawNumDelta;
        this.bottomDrawNumDelta = bottomDrawNumDelta;
        this.foodDelta = foodDelta;
        this.prestigeDelta = prestigeDelta;
        this.player = player;
    }

    @Override
    public void edit(SmallModel smallModel) {
        if(smallModel.getPlayer().getNickname().equals(player)) {
            if(foodDelta!=0) smallModel.getPlayer().setNumFood(smallModel.getPlayer().getNumFood() + foodDelta);
            if(prestigeDelta!=0) smallModel.getPlayer().setNumPrestige(smallModel.getPlayer().getNumPrestige() + prestigeDelta);
            if(bottomDrawNumDelta!=0) smallModel.setBottomDrawNum(bottomDrawNumDelta + smallModel.getBottomDrawNum());
            if(topDrawNumDelta!=0) smallModel.setTopDrawNum(topDrawNumDelta + smallModel.getTopDrawNum());
        }
        else{
            PlayerView view = smallModel.getOpponents().stream().filter(v -> v.getNickname().equals(player))
                    .findFirst().orElseThrow(IllegalStateException::new);
            if(foodDelta != 0) view.setNumFood(view.getNumFood() + foodDelta);
            if(prestigeDelta != 0) view.setNumPrestige(view.getNumPrestige() + prestigeDelta);
        }
    }
}
