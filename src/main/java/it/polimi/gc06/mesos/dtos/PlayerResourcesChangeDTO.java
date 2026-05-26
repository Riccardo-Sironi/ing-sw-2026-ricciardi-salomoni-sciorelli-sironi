package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

/**
 * Refreshes a player's resource stash (like food, prestige, or free draw counters) without touching the rest of the board.
 */
public class PlayerResourcesChangeDTO implements SmallModelEditor {

    private final String player;
    private final Integer topDrawNum;
    private final Integer bottomDrawNum;
    private final Integer food;
    private final Integer prestige;

    /**
     * Creates a targeted patch for a user's wallet.
     * @param player Target nickname.
     * @param topDrawNum New top draws allowed (can be null).
     * @param bottomDrawNum New bottom draws allowed (can be null).
     * @param food New food stash (can be null).
     * @param prestige New prestige count (can be null).
     */
    public PlayerResourcesChangeDTO(String player, Integer topDrawNum, Integer bottomDrawNum, Integer food, Integer prestige) {
        this.topDrawNum = topDrawNum;
        this.bottomDrawNum = bottomDrawNum;
        this.food = food;
        this.prestige = prestige;
        this.player = player;
    }

    /**
     * {@inheritDoc}
     * @param smallModel the client's small model.
     * @throws IllegalStateException if the indicated player doesn't exist locally.
     */
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

    /**
     * @return target player nickname.
     */
    public String getPlayer() {
        return player;
    }

    /**
     * {@inheritDoc}
     * @param visitor
     */
    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }
}
