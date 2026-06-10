package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Patch telling the client to wipe and replenish the buildings currently on display.
 */
public class BuildingsRefillDTO implements SmallModelEditor, Serializable {

    private final ArrayList<Card> top;
    private final ArrayList<Card> bottom;
    private Integer sequenceNumber;

    /**
     * @param top    The full top row of building cards.
     * @param bottom The full bottom row of building cards.
     */
    public BuildingsRefillDTO(Collection<Card> top, Collection<Card> bottom) {
        this.top = new ArrayList<>(top);
        this.bottom = new ArrayList<>(bottom);
        this.sequenceNumber = null;
    }

    /**
     * {@inheritDoc}
     *
     * @param smallModel the client's small model.
     */
    @Override
    public void edit(SmallModel smallModel) {
        smallModel.getTopBuildings().clear();
        smallModel.getTopBuildings().addAll(top);
        smallModel.getBottomBuildings().clear();
        smallModel.getBottomBuildings().addAll(bottom);
    }

    /**
     * {@inheritDoc}
     *
     * @param visitor
     */
    @Override
    public void accept(DTOVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @return the list of cards for the top building row.
     */
    public ArrayList<Card> getTop() {
        return top;
    }

    /**
     * @return the list of cards for the bottom building row.
     */
    public ArrayList<Card> getBottom() {
        return bottom;
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
