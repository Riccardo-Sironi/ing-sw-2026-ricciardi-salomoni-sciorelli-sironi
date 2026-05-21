package it.polimi.gc06.mesos.view.gui.visitors;

import it.polimi.gc06.mesos.dtos.*;
import it.polimi.gc06.mesos.view.gui.controllers.BoardController;
import it.polimi.gc06.mesos.view.gui.controllers.GameViewController;
import it.polimi.gc06.mesos.view.gui.controllers.LobbyGuiController;
import javafx.application.Platform;

import static it.polimi.gc06.mesos.view.gui.GUI.guiEventsManager;

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
    public void visit(LobbyInitializedDTO dto) {
        System.out.println("Lobby started!");
        Platform.runLater(() -> {
            lobbyGuiController.refreshLobbyUI();
            lobbyGuiController.checkAndStartGame();
        });
    }

    @Override
    public void visit(ChooseTotemColorDTO dto) {
        Platform.runLater(() -> {
            lobbyGuiController.refreshLobbyUI();
            lobbyGuiController.checkAndStartGame();
        });
    }

    @Override
    public void visit(MesosStartedDTO dto) {
        Platform.runLater(() -> {
            System.out.println("Game started!");
        });
    }

    @Override
    public void visit(TopRowRefillDTO dto) {
        System.out.println("Top row refill!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleTopRowRefill();
                guiEventsManager.onAnimationFinished();
            });
        });
    }

    @Override
    public void visit(BuildingsRefillDTO dto) {
        System.out.println("Building refill!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleTopBuildingsRefill();
                guiEventsManager.onAnimationFinished();
            });
        });
    }

    @Override
    public void visit(PickTopRowDTO dto) {
        System.out.println("Pick top row!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleTopRowPick(dto);
                guiEventsManager.onAnimationFinished();
            });
        });
    }

    @Override
    public void visit(PickBottomRowDTO dto) {
        System.out.println("Pick bottom row!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleBottomRowPick(dto);
                guiEventsManager.onAnimationFinished();
            });
        });
    }

    @Override
    public void visit(PickTopBuildingsDTO dto) {
        System.out.println("Pick top buildings!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleTopBuildingsPick(dto);
                guiEventsManager.onAnimationFinished();
            });
        });
    }

    @Override
    public void visit(PickBottomBuildingsDTO dto) {
        System.out.println("Pick bottom buildings!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleBottomBuildingsPick(dto);
                guiEventsManager.onAnimationFinished();
            });
        });
    }

    @Override
    public void visit(PlayerResourcesChangeDTO dto) {
        System.out.println("Player resources change!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handlePlayerResourcesChange(dto);
                guiEventsManager.onAnimationFinished();
            });
        });
    }

    @Override
    public void visit(PlayerStateChangeDTO dto) {
        System.out.println("Player state change!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleActivePlayerChanged();
                guiEventsManager.onAnimationFinished();
            });
        });
    }


    @Override
    public void visit(TotemOfferMoveDTO dto) {
        System.out.println("Totem moved!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleTotemMoved();
                guiEventsManager.onAnimationFinished();
            });
        });
    }

    @Override
    public void visit(TotemTurnMoveDTO dto) {
        System.out.println("Totem turn move!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleTotemTurnMove();
                guiEventsManager.onAnimationFinished();
            });
        });
    }

    @Override
    public void visit(PhaseChangeDTO dto) {
        System.out.println("Phase change!");

        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handlePhaseChanged();
                gameViewController.handlePhaseChanged();
                guiEventsManager.onAnimationFinished();
            });
        });
    }

    @Override
    public void visit(EraChangeDTO dto) {
        System.out.println("Era change!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleEraChanged();
                gameViewController.showEraOverlay();
                guiEventsManager.onAnimationFinished();
            });
        });
    }

    @Override
    public void visit(RoundChangeDTO dto) {
        System.out.println("Round change!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleRoundChanged();
                guiEventsManager.onAnimationFinished();
            });
        });
    }
}
