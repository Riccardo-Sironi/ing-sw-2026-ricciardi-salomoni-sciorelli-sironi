package it.polimi.gc06.mesos.view.gui.visitors;

import it.polimi.gc06.mesos.dtos.*;
import it.polimi.gc06.mesos.view.gui.controllers.BoardController;
import it.polimi.gc06.mesos.view.gui.controllers.GameViewController;
import it.polimi.gc06.mesos.view.gui.controllers.LobbyGuiController;
import javafx.application.Platform;

public class GUIDTOvisitor extends DTOvisitor {

    BoardController boardController;
    GameViewController gameViewController;
    LobbyGuiController lobbyGuiController;

    public GUIDTOvisitor() {
    }

    public void setLobbyGuiController(LobbyGuiController lobbyGuiController) {
        this.lobbyGuiController = lobbyGuiController;
    }

    public void setBoardController(BoardController boardController) {
        this.boardController = boardController;
    }

    public void setGameViewController(GameViewController gameViewController) {
        this.gameViewController = gameViewController;
    }

    @Override
    public void visit(SmallModelEditor dto) {
        super.visit(dto);
    }

    @Override
    public void visit(GameStateChangeDTO dto) {
        System.out.println("Game state change!");
        Platform.runLater(() -> {
            // TODO : we have to differentiate this changes somehow
            boardController.handleRoundChanged();
            boardController.handlePhaseChanged();
            boardController.handleEraChanged();
            gameViewController.handlePhaseChanged();
            //gameViewController.showEraOverlay();
        });
    }

    @Override
    public void visit(BuildingsRefillDTO dto) {
        System.out.println("Building refill!");
        Platform.runLater(() -> {
            boardController.handleTopBuildingsRefill();
        });
    }

    @Override
    public void visit(GameStartedDTO dto) {
        System.out.println("Game started!");
        Platform.runLater(() -> {
            boardController.handleGameStarted();
            // TODO : after drawing the board, show the era and phase overlays with a timer or something like that
            //gameViewController.showEraOverlay();
            gameViewController.showPhaseOverlay();
        });
    }

    @Override
    public void visit(PickBottomRowDTO dto) {
        System.out.println("Pick bottom row!");
        Platform.runLater(() -> {
            boardController.handleBottomRowPick();
        });
    }

    @Override
    public void visit(PickBottomBuildingsDTO dto) {
        System.out.println("Pick bottom buildings!");
        Platform.runLater(() -> {
            boardController.handleBottomBuildingsPick();
        });
    }

    @Override
    public void visit(PickTopRowDTO dto) {
        System.out.println("Pick top row!");
        Platform.runLater(() -> {
            boardController.handleTopRowPick();
        });
    }

    @Override
    public void visit(PickTopBuildingsDTO dto) {
        System.out.println("Pick top buildings!");
        Platform.runLater(() -> {
            boardController.handleTopBuildingsPick();
        });
    }

    @Override
    public void visit(PlayerResourcesChangeDTO dto) {
        System.out.println("Player resources change!");
        Platform.runLater(() -> {
            boardController.handlePlayerResourcesChange();
        });
    }

    @Override
    public void visit(PlayerStateChangeDTO dto) {
        System.out.println("Player state change!");
        Platform.runLater(() -> {
            boardController.handleActivePlayerChanged();
        });
    }

    @Override
    public void visit(TopRowRefillDTO dto) {
        System.out.println("Top row refill!");
        Platform.runLater(() -> {
            boardController.handleTopRowRefill();
        });
    }

    @Override
    public void visit(TotemMovedDTO dto) {
        System.out.println("Totem moved!");
        Platform.runLater(() -> {
            boardController.handleTotemMoved();
        });
    }
}
