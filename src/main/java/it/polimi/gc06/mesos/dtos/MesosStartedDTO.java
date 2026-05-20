package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class MesosStartedDTO implements SmallModelEditor {

    public MesosStartedDTO() {
    }

    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException {
    }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }

}
