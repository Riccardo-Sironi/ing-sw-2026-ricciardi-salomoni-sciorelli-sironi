package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.util.ArrayList;

/**
 * Signals the end of the game and updates the client's bottom row with the final state.
 */
public class EndGameDTO implements SmallModelEditor {
    private Integer sequenceNumber;
    private final ArrayList<Card> bottom;

    /**
     * Constructs a new EndGameDTO.
     *
     * @param bottom The final list of cards in the bottom row.
     */
    public EndGameDTO(ArrayList<Card> bottom) {
        this.bottom = bottom;
    }

    /**
     * {@inheritDoc}
     *
     * @param smallModel The client's small model.
     * @throws IllegalStateException If the internal state does not allow this operation.
     * @throws Error On critical failures.
     */
    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException, Error {
        smallModel.getBottomRow().clear();
        smallModel.getBottomRow().addAll(bottom);
    }

    /**
     * {@inheritDoc}
     *
     * @param visitor The visitor handling this DTO.
     */
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