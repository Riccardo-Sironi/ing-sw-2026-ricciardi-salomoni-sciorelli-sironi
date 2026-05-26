package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A dto package containing the fully replenished top (and consequently shifted bottom) display row of characters and events.
 */
public class TopRowRefillDTO implements SmallModelEditor, Serializable {

    private final ArrayList<Card> top; //we use an arraylist since collection might not be serializable
    private final ArrayList<Card> bottom;
    private final int deckSize;

    /**
     * @param top The refreshed top row payload.
     * @param bottom The latest bottom row (usually what slid down).
     * @param deckSize Remaining deck count in the reserve queue.
     */
    public TopRowRefillDTO(Collection<Card> top, Collection<Card> bottom, int deckSize) {
        this.top = new ArrayList<>(top);
        this.bottom = new ArrayList<>(bottom);
        this.deckSize = deckSize;
    }

    /**
     * {@inheritDoc}
     * @param smallModel the client's small model.
     */
    @Override
    public void edit(SmallModel smallModel) {
        smallModel.getTopRow().clear();
        smallModel.getTopRow().addAll(top);
        smallModel.getBottomRow().clear();
        smallModel.getBottomRow().addAll(bottom);
        smallModel.setTribeDeckSize(deckSize);
    }

    /**
     * {@inheritDoc}
     * @param visitor
     */
    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @return the new top row cards to append.
     */
    public ArrayList<Card> getTop() {
        return top;
    }

    /**
     * @return the cards that got flushed down to the bottom tier.
     */
    public ArrayList<Card> getBottom() {
        return bottom;
    }
}
