package it.polimi.gc06.mesos.DTOs;

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

    public void visit(TotemMovedDTO dto) {
        visit((SmallModelEditor) dto);
    }
}