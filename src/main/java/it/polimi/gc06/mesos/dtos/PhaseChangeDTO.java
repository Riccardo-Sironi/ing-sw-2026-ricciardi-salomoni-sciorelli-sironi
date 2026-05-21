package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class PhaseChangeDTO implements SmallModelEditor {
    private final String phase;

    public PhaseChangeDTO(String phase) {
        this.phase = phase;
    }

    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException, Error {
        smallModel.setPhase(phase);
    }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }

    public String getPhase() {
        return phase;
    }
}
