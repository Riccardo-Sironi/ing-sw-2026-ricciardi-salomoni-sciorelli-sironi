package it.polimi.gc06.mesos.view.gui.visitors;

import it.polimi.gc06.mesos.dtos.*;
import it.polimi.gc06.mesos.view.gui.controllers.BoardController;

public class GUIDTOvisitor extends DTOvisitor {

    BoardController boardController;

    public GUIDTOvisitor(BoardController boardController) {
        this.boardController = boardController;
    }

    @Override
    public void visit(SmallModelEditor dto) {
        super.visit(dto);
    }

    @Override
    public void visit(GameStateChangeDTO dto) {
        boardController.handleRoundChanged();
        // handleEraChanged
        // handlePhaseChanged
    }

    @Override
    public void visit(BuildingsRefillDTO dto) {
        boardController.handleTopBuildingsRefill();
    }

    @Override
    public void visit(GameStartedDTO dto) {
        super.visit(dto);
    }

    @Override
    public void visit(PickBottomRowDTO dto) {
        boardController.handleBottomRowPick();
    }

    @Override
    public void visit(PickBottomBuildingsDTO dto) {
        boardController.handleBottomBuildingsPick();
    }

    @Override
    public void visit(PickTopRowDTO dto) {
        boardController.handleTopRowPick();
    }

    @Override
    public void visit(PickTopBuildingsDTO dto) {
        boardController.handleTopBuildingsPick();
    }

    @Override
    public void visit(PlayerResourcesChangeDTO dto) {
        super.visit(dto);
    }

    @Override
    public void visit(PlayerStateChangeDTO dto) {
        boardController.handleActivePlayerChanged();
    }

    @Override
    public void visit(TopRowRefillDTO dto) {
        boardController.handleTopRowRefill();
    }

    @Override
    public void visit(TotemMovedDTO dto) {
        boardController.handleTotemMoved();
    }
}
