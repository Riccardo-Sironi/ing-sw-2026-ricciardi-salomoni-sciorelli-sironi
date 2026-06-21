package it.polimi.gc06.mesos.view.gui.visitors;

import it.polimi.gc06.mesos.dtos.*;
import it.polimi.gc06.mesos.view.gui.GUI;
import it.polimi.gc06.mesos.view.gui.GameScene;
import it.polimi.gc06.mesos.view.gui.ImageFetcher;
import it.polimi.gc06.mesos.view.gui.controllers.BoardController;
import it.polimi.gc06.mesos.view.gui.controllers.GameViewController;
import it.polimi.gc06.mesos.view.gui.controllers.LobbyGuiController;
import javafx.application.Platform;

import static it.polimi.gc06.mesos.view.gui.GUI.guiEventsManager;

public class GUIDTOVisitor extends DTOVisitor {

    BoardController boardController;
    GameViewController gameViewController;
    LobbyGuiController lobbyGuiController;

    public GUIDTOVisitor() {
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

        if (lobbyGuiController != null) {
            Platform.runLater(() -> {
                lobbyGuiController.refreshLobbyUI();
                lobbyGuiController.checkAndStartGame();
            });
        }

        if (boardController != null) {
            Platform.runLater(() -> {
                boardController.drawEverything();
            });
        }
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
    public void visit(PlayerJoinedLobbyDTO dto) {
        System.out.println("New player joined the lobby!");

        if (lobbyGuiController != null) {
            Platform.runLater(() -> {
                lobbyGuiController.refreshLobbyUI();
            });
        }
    }

    @Override
    public void visit(GameResumeDTO dto) {
        System.out.println("Game should be resumed, redirecting to GameView...");
        Platform.runLater(() -> {
            try {
                System.out.println("SmallModel received: "+dto);
                if (GUI.imageFetcher == null) {
                    int totalPlayers = GUI.smallModel.getOpponents().size() + 1;
                    GUI.imageFetcher = new ImageFetcher(totalPlayers);
                }
                GUI.changeScene(GameScene.GAME.getPath());
                boardController.drawEverything();
            } catch (Exception e) {
                System.err.println("Error during GameView redirecting:");
                e.printStackTrace();
            }
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
        if (boardController == null) return;
        System.out.println("Top row refill!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleTopRowRefill(dto, () -> {
                    guiEventsManager.onAnimationFinished();
                });
            });
        });
    }

    @Override
    public void visit(BuildingsRefillDTO dto) {
        if (boardController == null) return;
        System.out.println("Building refill!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleTopBuildingsRefill(dto, () -> {
                    guiEventsManager.onAnimationFinished();
                });
            });
        });
    }

    @Override
    public void visit(PickTopRowDTO dto) {
        System.out.println("Pick top row!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleTopRowPick(dto, () -> {
                    guiEventsManager.onAnimationFinished();
                });
            });
        });
    }

    @Override
    public void visit(PickBottomRowDTO dto) {
        System.out.println("Pick bottom row!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleBottomRowPick(dto, () -> {
                    guiEventsManager.onAnimationFinished();
                });
            });
        });
    }

    @Override
    public void visit(PickTopBuildingsDTO dto) {
        System.out.println("Pick top buildings!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleTopBuildingsPick(dto, () -> {
                    guiEventsManager.onAnimationFinished();
                });
            });
        });
    }

    @Override
    public void visit(PickBottomBuildingsDTO dto) {
        System.out.println("Pick bottom buildings!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleBottomBuildingsPick(dto, () -> {
                    guiEventsManager.onAnimationFinished();
                });
            });
        });
    }

    @Override
    public void visit(PlayerResourcesChangeDTO dto) {
        if (boardController == null) return;
        System.out.println("Player resources change!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handlePlayerResourcesChange(dto);
                guiEventsManager.onAnimationFinished();
            });
        });
    }

    @Override
    public void visit(PlayerRecapDTO dto) {
        if (boardController == null) return;
        System.out.println("Player recap arrived!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handlePlayerRecap(dto);
                guiEventsManager.onAnimationFinished();
            });
        });
    }

    @Override
    public void visit(PlayerStateChangeDTO dto) {
        if (boardController == null) return;
        System.out.println("Player state change!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleActivePlayerChanged(dto);
                guiEventsManager.onAnimationFinished();
            });
        });
    }

    @Override
    public void visit(TotemOfferMoveDTO dto) {
        System.out.println("Totem moved!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleTotemMoved(dto, () -> {
                    guiEventsManager.onAnimationFinished();
                });
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
        if (boardController == null) return;
        if (gameViewController == null) return;
        System.out.println("Phase change!" + dto.getPhase());
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                gameViewController.showPhaseOverlay(dto.getPhase(), () -> {
                    boardController.handlePhaseChanged(dto.getPhase());
                    guiEventsManager.onAnimationFinished();
                });
            });
        });
    }

    @Override
    public void visit(EraChangeDTO dto) {
        if (boardController == null) return;
        if (gameViewController == null) return;
        System.out.println("Era change!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                gameViewController.showEraOverlay(dto.getEra(), () -> {
                    gameViewController.drawEraBackground();
                    boardController.handleEraChanged();
                    guiEventsManager.onAnimationFinished();
                });
            });
        });
    }

    @Override
    public void visit(RoundChangeDTO dto) {
        if (boardController == null) return;
        System.out.println("Round change!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleRoundChanged();
                guiEventsManager.onAnimationFinished();
            });
        });
    }

    @Override
    public void visit(EventResolvedDTO dto) {
        if (boardController == null) return;
        System.out.println("Event resolved!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleEventResolved(dto, () -> {
                    guiEventsManager.onAnimationFinished();
                });
            });
        });
    }

    @Override
    public void visit(LeaderboardChangeDTO dto) {
        if (gameViewController == null) return;
        System.out.println("Leaderboard change!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                gameViewController.handleLeaderboardChange();
                guiEventsManager.onAnimationFinished();
            });
        });
    }

    @Override
    public void visit(EndGameDTO dto) {
        if (gameViewController == null) return;
        System.out.println("Game ended!");
        Platform.runLater(() -> {
            guiEventsManager.enqueueEvent(() -> {
                boardController.handleEndGame();
                gameViewController.handleEndGame(() -> {
                    guiEventsManager.onAnimationFinished();
                });
            });
        });
    }

}
