package it.polimi.gc06.mesos.view.gui.visitors;

import it.polimi.gc06.mesos.DTOs.*;
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
        super.visit(dto);
    }

    @Override
    public void visit(BuildingsRefillDTO dto) {
        super.visit(dto);
    }

    @Override
    public void visit(GameStartedDTO dto) {
        super.visit(dto);
    }

    @Override
    public void visit(PickBottomRowDTO dto) {
        super.visit(dto);
    }

    @Override
    public void visit(PickBottomBuildingsDTO dto) {
        super.visit(dto);
    }

    @Override
    public void visit(PickTopRowDTO dto) {
        super.visit(dto);
    }

    @Override
    public void visit(PickTopBuildingsDTO dto) {
        super.visit(dto);
    }

    @Override
    public void visit(PlayerResourcesChangeDTO dto) {
        super.visit(dto);
    }

    @Override
    public void visit(PlayerStateChangeDTO dto) {
        super.visit(dto);
    }

    @Override
    public void visit(TopRowRefillDTO dto) {
        super.visit(dto);
    }

    @Override
    public void visit(TotemMovedDTO dto) {
        super.visit(dto);
    }
}
