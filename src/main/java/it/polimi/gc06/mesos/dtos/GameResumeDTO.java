package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;

/**
 * Transmits a complete snapshot of the game state to allow a client
 * to resume an ongoing match after a disconnection or crash.
 */
public class GameResumeDTO implements SmallModelEditor{

    private final SmallModel resumeReference;

    /**
     * Constructs a new GameResumeDTO.
     *
     * @param resumeReference The complete small model representing the saved game state.
     */
    public GameResumeDTO(SmallModel resumeReference){
        this.resumeReference = resumeReference;
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
        smallModel.copy(resumeReference);
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
    public void setSequenceNumber(int sNum) {}

    @Override
    public Integer getSequenceNumber() {
        return null;
    }

    /**
     * Returns the string representation of the resumed model state.
     *
     * @return The string representation.
     */
    @Override
    public String toString(){
        return resumeReference.toString();
    }
}