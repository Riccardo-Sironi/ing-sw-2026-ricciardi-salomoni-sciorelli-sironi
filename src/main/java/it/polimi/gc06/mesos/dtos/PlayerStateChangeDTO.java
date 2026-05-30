package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;

/**
 * Signals when a player's turn status switches on or off, or if they unlock the ability to skip.
 */
public class PlayerStateChangeDTO implements SmallModelEditor {

    private final String player;
    private int isActive;
    private int canSkip;
    private Integer sequenceNumber;

    /**
     * @param player The Nickname whose turn states are being configured.
     */
    public PlayerStateChangeDTO(String player) {
        this.player = player;
        this.isActive = -1;
        this.canSkip = -1;
        this.sequenceNumber = null;
    }

    /**
     * Toggles whether the active player state is on or off.
     * @param isActive Boolean indicating if the player should be considered active.
     */
    public void setIsActive(boolean isActive) {
        this.isActive = isActive ? 1 : 0;
    }

    /**
     * Toggles whether the player is allowed to hit the skip button or not.
     * @param canSkip Boolean indicating skipping allowance.
     */
    public void setCanSkip(boolean canSkip) {
        this.canSkip = canSkip ? 1 : 0;
    }

    /**
     * {@inheritDoc}
     * @param smallModel the client's small model.
     * @throws IllegalStateException if the state update violates integrity.
     */
    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException {
        if (!smallModel.getPlayer().getNickname().equals(player)) {
            smallModel.setActive(false);
            smallModel.setCanSkip(false);
        } else {
            if (isActive != -1) smallModel.setActive(isActive == 1);
            if (canSkip != -1) smallModel.setCanSkip(canSkip == 1);
        }
    }

    /**
     * {@inheritDoc}
     * @param visitor
     */
    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @return the player alias.
     */
    public String getPlayer() {
        return player;
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
