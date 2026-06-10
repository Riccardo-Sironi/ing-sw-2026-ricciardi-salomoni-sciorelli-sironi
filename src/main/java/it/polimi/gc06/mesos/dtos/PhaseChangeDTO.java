package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;

/**
 * Notifies the client that the game state machine has stepped into a new phase.
 */
public class PhaseChangeDTO implements SmallModelEditor {
    private final String phase;
    private Integer sequenceNumber;

    /**
     * @param phase The string name of the new active game phase.
     */
    public PhaseChangeDTO(String phase) {
        this.phase = phase;
        this.sequenceNumber = null;
    }

    /**
     * {@inheritDoc}
     *
     * @param smallModel the client's small model.
     * @throws IllegalStateException when the phase transition is not allowed.
     * @throws Error                 if phase resolution fails entirely.
     */
    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException, Error {
        smallModel.setPhase(phase);
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
     * @return the string representing this new phase.
     */
    public String getPhase() {
        return phase;
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
