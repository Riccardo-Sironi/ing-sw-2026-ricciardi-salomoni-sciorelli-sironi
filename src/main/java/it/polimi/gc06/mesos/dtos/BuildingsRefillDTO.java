package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class BuildingsRefillDTO implements SmallModelEditor, Serializable {

    private final ArrayList<Card> top;
    private final ArrayList<Card> bottom;
    private final int deckSize;

    public BuildingsRefillDTO(Collection<Card> top, Collection<Card> bottom, int deckSize) {
        this.top = new ArrayList<>(top);
        this.bottom = new ArrayList<>(bottom);
        this.deckSize = deckSize;
    }

    @Override
    public void edit(SmallModel smallModel) {
        // TODO : this is almost neve called (FIX)
        smallModel.getTopBuildings().clear();
        smallModel.getTopBuildings().addAll(top);
        smallModel.getBottomBuildings().clear();
        smallModel.getBottomBuildings().addAll(bottom);
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
