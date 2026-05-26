package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class TotemTurnMoveDTO implements SmallModelEditor {

    public TotemTurnMoveDTO() {
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
        int turnOrderCounter = 0;
        // TODO Si potrebbe fare in maniera più carina con programmazione funzionale?
        for (int i = 0; i < smallModel.getOfferTrack().size(); i++) {
            if (smallModel.getOfferTrack().get(i).getPlayer() == null) continue;
            smallModel.getTurnOrderTile().set(turnOrderCounter, smallModel.getOfferTrack().get(i).removePlayer());
            turnOrderCounter++;
        }
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
