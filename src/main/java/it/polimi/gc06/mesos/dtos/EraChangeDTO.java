package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

/**
 * Alerts the client that times are changing and a new Era has begun.
 */
public class EraChangeDTO implements SmallModelEditor {

    private final Era era;

    /**
     * @param era The newly activated era index.
     */
    public EraChangeDTO(Era era) {
        this.era = era;
    }

    /**
     * {@inheritDoc}
     * @param smallModel the client's small model.
     * @throws IllegalStateException when state is inconsistent.
     * @throws Error on severe faults.
     */
    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException, Error {
        smallModel.setEra(era);
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
     * @return the newly activated era.
     */
    public Era getEra() {
        return era;
    }
}
