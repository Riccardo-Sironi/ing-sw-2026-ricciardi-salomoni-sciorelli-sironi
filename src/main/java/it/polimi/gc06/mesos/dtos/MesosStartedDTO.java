package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;

/**
 * Lightweight ping indicating the final 'Mesos is running' state. Basically empty, just triggers UI handlers.
 */
public class MesosStartedDTO implements SmallModelEditor {

    public MesosStartedDTO() {
    }

    /**
     * {@inheritDoc}
     * @param smallModel the client's small model.
     * @throws IllegalStateException on state conflicts.
     */
    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException {
    }

    /**
     * {@inheritDoc}
     * @param visitor
     */
    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }

}
