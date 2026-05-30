package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

/**
 * Tells the UI that a player successfully locked their totem into a slot on the offer track.
 */
public class TotemOfferMoveDTO implements SmallModelEditor {

    private final String player;
    private final int index;
    private Integer sequenceNumber;

    /**
     * @param player The active player setting their totem down.
     * @param index  Which exact index slot they stole on the track.
     */
    public TotemOfferMoveDTO(String player, int index) {
        this.player = player;
        this.index = index;
        this.sequenceNumber = null;
    }

    /**
     * {@inheritDoc}
     *
     * @param smallModel the client's small model.
     * @throws IllegalStateException if the player performing the placing doesn't exist.
     */
    @Override
    public void edit(SmallModel smallModel) {
        PlayerView view;
        if (smallModel.getPlayer().getNickname().equals(player)) {
            view = smallModel.getPlayer();
        } else {
            view = smallModel.getOpponents().stream().filter(v -> v.getNickname().equals(player))
                    .findFirst().orElseThrow(IllegalStateException::new);
        }
        //finds old pos and removes
        for (int i = 0; i < smallModel.getTurnOrderTile().size(); i++) {
            if (smallModel.getTurnOrderTile().get(i) != null && smallModel.getTurnOrderTile().get(i).getNickname().equals(player)) {
                smallModel.getTurnOrderTile().set(i, null);
                break;
            }
        }
        //puts on new pos
        smallModel.getOfferTrack().get(index).setPlayer(view);
    }

    /**
     * {@inheritDoc}
     *
     * @param visitor
     */
    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @return which slot the track was pinned on.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return the player that acted.
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
