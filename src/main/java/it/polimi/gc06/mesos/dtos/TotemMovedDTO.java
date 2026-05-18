package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class TotemMovedDTO implements SmallModelEditor {

    private final String player;
    private final int index;

    public TotemMovedDTO(String player, int index) {
        this.player = player;
        this.index = index;
    }

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
            if (smallModel.getTurnOrderTile().get(i) == null || smallModel.getTurnOrderTile().get(i).getNickname().equals(player)) {
                smallModel.getTurnOrderTile().set(i, null);
            }
        }
        //puts on new pos
        smallModel.getOfferTrack().get(index).setPlayer(view);
    }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }
}
