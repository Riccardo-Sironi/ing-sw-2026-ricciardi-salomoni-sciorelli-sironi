package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class TotemTurnMoveDTO implements SmallModelEditor {

    public TotemTurnMoveDTO() {
    }

    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException, Error {
        int turnOrderCounter = 0;
        for (int i = 0; i < smallModel.getOfferTrack().size(); i++) {
            if (smallModel.getOfferTrack().get(i).getPlayer() == null) continue;
            smallModel.getTurnOrderTile().set(turnOrderCounter, smallModel.getOfferTrack().get(i).removePlayer());
            turnOrderCounter++;
        }
    }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }
}
