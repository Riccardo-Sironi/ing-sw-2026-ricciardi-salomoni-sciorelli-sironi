package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class BuildingsRefillDTO implements SmallModelEditor, Serializable {

    private final ArrayList<Card> top;
    private final ArrayList<Card> bottom;

    public BuildingsRefillDTO(Collection<Card> top, Collection<Card> bottom) {
        this.top = new ArrayList<>(top);
        this.bottom = new ArrayList<>(bottom);
    }

    @Override
    public void edit(SmallModel smallModel) {
        // TODO : this is almost neve called (FIX)
        smallModel.getTopBuildings().clear();
        smallModel.getTopBuildings().addAll(top);
        smallModel.getBottomBuildings().clear();
        smallModel.getBottomBuildings().addAll(bottom);
    }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }

}
