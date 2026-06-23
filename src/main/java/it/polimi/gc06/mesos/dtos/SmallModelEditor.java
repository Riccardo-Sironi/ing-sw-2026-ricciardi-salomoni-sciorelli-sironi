package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.io.Serializable;

/**
 * The core contract for any update packet hopping over the network.
 * Needs to know how to patch its changes into the local SmallModel.
 */
public interface SmallModelEditor extends Serializable {

    /**
     * Applies this tiny diff straight onto the live client model.
     *
     * @param smallModel The client's small model.
     * @throws IllegalStateException When the internal state doesn't allow this change.
     * @throws Error On critical failures during the application.
     */
    void edit(SmallModel smallModel) throws IllegalStateException, Error;

    /**
     * Lets an external visitor poke at the actual concrete DTO without gross casts.
     *
     * @param visitor The visitor handling this DTO.
     */
    void accept(DTOVisitor visitor);

    /**
     * Set the sequence number for the DTO.
     *
     * @param sNum The sequence number.
     */
    void setSequenceNumber(int sNum);

    /**
     * Sequence number getter.
     *
     * @return The sequence number of the DTO, if {@code null} the DTO is
     * not a global DTO and the sequence is irrelevant.
     */
    Integer getSequenceNumber();
}