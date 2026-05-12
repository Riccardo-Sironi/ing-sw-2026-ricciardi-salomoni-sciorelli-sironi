package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class ErrorDTO implements SmallModelEditor{

    private final String message;

    public ErrorDTO(String message){
        this.message = message;
    }

    @Override
    public void edit(SmallModel smallModel) throws Error {
        throw new Error(message);
    }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }
}
