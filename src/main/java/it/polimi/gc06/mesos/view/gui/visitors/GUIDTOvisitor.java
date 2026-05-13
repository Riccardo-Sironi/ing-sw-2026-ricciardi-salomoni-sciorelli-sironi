package it.polimi.gc06.mesos.view.gui.visitors;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.dtos.*;
import it.polimi.gc06.mesos.view.gui.controllers.BoardController;
import it.polimi.gc06.mesos.view.gui.controllers.GameViewController;
import it.polimi.gc06.mesos.view.gui.controllers.LobbyGuiController;
import javafx.application.Platform;

public class GUIDTOvisitor extends DTOvisitor {

    BoardController boardController;
    GameViewController gameViewController;
    LobbyGuiController lobbyGuiController;

    public GUIDTOvisitor(BoardController boardController, GameViewController gameViewController, LobbyGuiController lobbyGuiController) {
        this.boardController = boardController;
        this.gameViewController = gameViewController;
        this.lobbyGuiController = lobbyGuiController;
    }

    @Override
    public void visit(SmallModelEditor dto) {
        super.visit(dto);
    }

    @Override
    public void visit(GameStateChangeDTO dto) {
        Platform.runLater(() -> {
            boardController.handleRoundChanged();
            // handleEraChanged
            // handlePhaseChanged
        });
    }

    @Override
    public void visit(BuildingsRefillDTO dto) {
        Platform.runLater(() -> {
            boardController.handleTopBuildingsRefill();
        });
    }

    @Override
    public void visit(GameStartedDTO dto) {
        super.visit(dto);
    }

    @Override
    public void visit(PickBottomRowDTO dto) {
        Platform.runLater(() -> {
            boardController.handleBottomRowPick();
        });
    }

    @Override
    public void visit(PickBottomBuildingsDTO dto) {
        Platform.runLater(() -> {
            boardController.handleBottomBuildingsPick();
        });
    }

    @Override
    public void visit(PickTopRowDTO dto) {
        Platform.runLater(() -> {
            boardController.handleTopRowPick();
        });
    }

    @Override
    public void visit(PickTopBuildingsDTO dto) {
        Platform.runLater(() -> {
            boardController.handleTopBuildingsPick();
        });
    }

    @Override
    public void visit(PlayerResourcesChangeDTO dto) {
        super.visit(dto);
    }

    @Override
    public void visit(PlayerStateChangeDTO dto) {
        Platform.runLater(() -> {
            boardController.handleActivePlayerChanged();
        });
    }

    @Override
    public void visit(TopRowRefillDTO dto) {
        Platform.runLater(() -> {
            boardController.handleTopRowRefill();
        });
    }

    @Override
    public void visit(TotemMovedDTO dto) {
        Platform.runLater(() -> {
            boardController.handleTotemMoved();
        });
    }
}
