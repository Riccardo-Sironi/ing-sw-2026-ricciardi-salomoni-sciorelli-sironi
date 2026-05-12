package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.io.Serializable;

public interface SmallModelEditor extends Serializable {

    void edit(SmallModel smallModel) throws IllegalStateException, Error;

}
