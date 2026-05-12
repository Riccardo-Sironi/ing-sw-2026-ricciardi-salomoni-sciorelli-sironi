package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class BuildingsRefillDTO implements SmallModelEditor, Serializable {

    private final ArrayList<Card> cards; //we use an arraylist since collection might not be serializable

    public BuildingsRefillDTO(Collection<Card> cards) {
        this.cards = new ArrayList<>(cards);
    }

    @Override
    public void edit(SmallModel smallModel) {
        smallModel.getTopBuildings().addAll(cards);
    }
}
