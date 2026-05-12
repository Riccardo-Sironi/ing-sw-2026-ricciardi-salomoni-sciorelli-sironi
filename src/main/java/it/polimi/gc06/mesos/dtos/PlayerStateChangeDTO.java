package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class PlayerStateChangeDTO implements SmallModelEditor{

    private final String player;
    private int isActive;
    private int canSkip;

    public PlayerStateChangeDTO(String player) {
        this.player = player;
        this.isActive = -1;
        this.canSkip = -1;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive ? 1 : 0;
    }

    public void setCanSkip(boolean canSkip) {
        this.canSkip = canSkip ? 1 : 0;
    }

    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException {
        if(!smallModel.getPlayer().getNickname().equals(player)) return;
        if(isActive == 1 || isActive == 0) smallModel.setActive(isActive == 1);
        if(canSkip == 1 || canSkip == 0) smallModel.setCanSkip(canSkip == 1);
    }
}
