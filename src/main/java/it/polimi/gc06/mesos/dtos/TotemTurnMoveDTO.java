package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

/**
 * Broadcasts that a player's totem has been moved back from the offer track
 * to a specific index on the turn order tile.
 */
public class TotemTurnMoveDTO implements SmallModelEditor {

    private int index;
    private String player;
    private Integer sequenceNumber;

    /**
     * Constructs a new TotemTurnMoveDTO.
     *
     * @param player The nickname of the player whose totem is moving.
     * @param index The target slot index on the turn order tile.
     */
    public TotemTurnMoveDTO(String player, int index) {
        this.player = player;
        this.index = index;
        this.sequenceNumber = null;
    }

    /**
     * {@inheritDoc}
     *
     * @param smallModel The client's small model.
     * @throws IllegalStateException If shuffling fails due to broken slot assumptions.
     * @throws Error On failure.
     */
    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException, Error {
        PlayerView view;
        if (smallModel.getPlayer().getNickname().equals(player)) {
            view = smallModel.getPlayer();
        } else {
            view = smallModel.getOpponents().stream().filter(v -> v.getNickname().equals(player))
                    .findFirst().orElseThrow(IllegalStateException::new);
        }

        for (int i = 0; i < smallModel.getOfferTrack().size(); i++) {
            if (smallModel.getOfferTrack().get(i).getPlayer() != null && smallModel.getOfferTrack().get(i).getPlayer().getNickname().equals(player)) {
                smallModel.getOfferTrack().get(i).removePlayer();
                break;
            }
        }

        smallModel.getTurnOrderTile().set(index, view);
    }

    /**
     * {@inheritDoc}
     *
     * @param visitor The visitor handling this DTO.
     */
    @Override
    public void accept(DTOVisitor visitor) {
        visitor.visit(this);
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