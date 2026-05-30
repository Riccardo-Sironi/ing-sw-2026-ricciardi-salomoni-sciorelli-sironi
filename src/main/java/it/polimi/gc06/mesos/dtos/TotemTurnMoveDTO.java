package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class TotemTurnMoveDTO implements SmallModelEditor {

    private int index;
    private String player;

    public TotemTurnMoveDTO(String player, int index) {
        this.player = player;
        this.index = index;
    }

    /**
     * {@inheritDoc}
     *
     * @param smallModel the client's small model.
     * @throws IllegalStateException if shuffling fails due to broken slot assumptions.
     * @throws Error                 on failure.
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
     * @param visitor
     */
    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }
}
