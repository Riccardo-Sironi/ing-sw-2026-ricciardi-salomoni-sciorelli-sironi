package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;

/**
 * Packs up server errors into a tiny DTO throwable brick that prints right on the client interface.
 */
public class ErrorDTO implements SmallModelEditor{

    private final String message;
    private Integer sequenceNumber;

    /**
     * @param message Text copy detailing why it errored out.
     */
    public ErrorDTO(String message){
        this.message = message;
        this.sequenceNumber = null;
    }

    /**
     * {@inheritDoc}
     * @param smallModel the client's small model.
     * @throws Error always throws an error conveying the internal message.
     */
    @Override
    public void edit(SmallModel smallModel) throws Error {
        throw new Error(message);
    }

    /**
     * {@inheritDoc}
     * @param visitor
     */
    @Override
    public void accept(DTOvisitor visitor) {
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
