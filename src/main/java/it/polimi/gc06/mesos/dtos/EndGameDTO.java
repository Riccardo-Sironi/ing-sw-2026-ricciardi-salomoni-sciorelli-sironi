package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.util.ArrayList;

public class EndGameDTO implements SmallModelEditor {
    private Integer sequenceNumber;
    private final ArrayList<Card> bottom;

    public EndGameDTO(ArrayList<Card> bottom) {
        this.bottom = bottom;
    }

    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException, Error {
        smallModel.getBottomRow().clear();
        smallModel.getBottomRow().addAll(bottom);
    }

    @Override
    public void accept(DTOVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void setSequenceNumber(int sNum) {
        this.sequenceNumber = sNum;
    }

    @Override
    public Integer getSequenceNumber() {
        return this.sequenceNumber;
    }
}
