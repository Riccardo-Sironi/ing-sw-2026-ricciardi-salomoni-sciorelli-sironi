package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class EraChangeDTO implements SmallModelEditor {

    private final Era era;

    public EraChangeDTO(Era era) {
        this.era = era;
    }

    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException, Error {
        smallModel.setEra(era);
    }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }

    public Era getEra() {
        return era;
    }
}
