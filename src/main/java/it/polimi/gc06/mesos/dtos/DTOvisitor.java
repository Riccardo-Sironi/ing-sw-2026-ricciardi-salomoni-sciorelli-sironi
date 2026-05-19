package it.polimi.gc06.mesos.dtos;

public abstract class DTOvisitor {

    public void visit(SmallModelEditor dto) {
    }

    public void visit(GameStateChangeDTO dto) {
        visit((SmallModelEditor) dto);
    }

    public void visit(BuildingsRefillDTO dto) {
        visit((SmallModelEditor) dto);
    }

    public void visit(GameStartedDTO dto) {
        visit((SmallModelEditor) dto);
    }

    public void visit(PickBottomRowDTO dto) {
        visit((SmallModelEditor) dto);
    }

    public void visit(PickBottomBuildingsDTO dto) {
        visit((SmallModelEditor) dto);
    }

    public void visit(PickTopRowDTO dto) {
        visit((SmallModelEditor) dto);
    }

    public void visit(PickTopBuildingsDTO dto) {
        visit((SmallModelEditor) dto);
    }

    public void visit(PlayerResourcesChangeDTO dto) {
        visit((SmallModelEditor) dto);
    }

    public void visit(PlayerStateChangeDTO dto) {
        visit((SmallModelEditor) dto);
    }

    public void visit(TopRowRefillDTO dto) {
        visit((SmallModelEditor) dto);
    }

    public void visit(TotemOfferMoveDTO dto) {
        visit((SmallModelEditor) dto);
    }

    public void visit(PhaseChangeDTO dto) {
        visit((SmallModelEditor) dto);
    }

    public void visit(EraChangeDTO dto) {
        visit((SmallModelEditor) dto);
    }

    public void visit(RoundChangeDTO dto) {
        visit((SmallModelEditor) dto);
    }

    public void visit(TotemTurnMoveDTO dto) {
        visit((SmallModelEditor) dto);
    }
}