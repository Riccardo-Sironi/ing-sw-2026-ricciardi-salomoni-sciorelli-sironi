package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class TopRowRefillDTO implements SmallModelEditor, Serializable {

    private final ArrayList<Card> top; //we use an arraylist since collection might not be serializable
    private final ArrayList<Card> bottom;
    private final int deckSize;

    public TopRowRefillDTO(Collection<Card> top, Collection<Card> bottom, int deckSize) {
        this.top = new ArrayList<>(top);
        this.bottom = new ArrayList<>(bottom);
        this.deckSize = deckSize;
    }

    @Override
    public void edit(SmallModel smallModel) {
        smallModel.getTopRow().clear();
        smallModel.getTopRow().addAll(top);
        smallModel.getBottomRow().clear();
        smallModel.getBottomRow().addAll(bottom);
        smallModel.setTribeDeckSize(deckSize);
    }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }

    public ArrayList<Card> getTop() {
        return top;
    }

    public ArrayList<Card> getBottom() {
        return bottom;
    }
}
