package it.polimi.gc06.mesos.dtos;

/**
 * Lightweight dto alerting the client model to tick the round counter up.
 */
public class RoundChangeDTO implements SmallModelEditor {

    private final int round;
    private Integer sequenceNumber;

    public RoundChangeDTO(int round) {
        this.round = round;
        this.sequenceNumber = null;
    }

    /**
     * {@inheritDoc}
     *
     * @param smallModel the client's small model.
     * @throws IllegalStateException if round scaling is locked.
     * @throws Error on critical failure.
     */
    @Override
    public void edit(it.polimi.gc06.mesos.view.smallModel.SmallModel smallModel) throws IllegalStateException, Error {
        smallModel.setRound(round);
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

    @Override
    public void setSequenceNumber(int sNum) {
        sequenceNumber = sNum;
    }

    @Override
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }
}
