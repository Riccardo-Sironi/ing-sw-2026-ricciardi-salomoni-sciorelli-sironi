package it.polimi.gc06.mesos.dtos;

public class RoundChangeDTO implements SmallModelEditor {

    private final int round;

    public RoundChangeDTO(int round) {
        this.round = round;
    }

    @Override
    public void edit(it.polimi.gc06.mesos.view.smallModel.SmallModel smallModel) throws IllegalStateException, Error {
        smallModel.setRound(round);
    }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }
}

