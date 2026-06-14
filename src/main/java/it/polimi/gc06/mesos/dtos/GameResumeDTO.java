package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class GameResumeDTO implements SmallModelEditor{

    private final SmallModel resumeReference;

    public GameResumeDTO(SmallModel resumeReference){
        this.resumeReference = resumeReference;
    }


    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException, Error {
        smallModel.copy(resumeReference);
    }

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

    @Override
    public String toString(){
        return resumeReference.toString();
    }
}
