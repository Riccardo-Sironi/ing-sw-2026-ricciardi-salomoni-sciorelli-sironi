package it.polimi.gc06.mesos.view.gui;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.InstancesManager.ModelInstancesManager;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.view.View;
import it.polimi.gc06.mesos.view.gui.controllers.BoardController;
import it.polimi.gc06.mesos.view.gui.controllers.GameViewController;
import it.polimi.gc06.mesos.view.gui.controllers.LobbyGuiController;
import it.polimi.gc06.mesos.view.gui.visitors.GUIDTOvisitor;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import it.polimi.gc06.mesos.view.smallModel.TileSlotView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class GUI extends Application implements View {

    public static int WIDTH = 1920;
    public static int HEIGHT = 1080;

    public static SmallModel smallModel;
    public static ImageFetcher imageFetcher;

    public static GameViewController gameViewController;
    public static BoardController boardController;
    public static LobbyGuiController lobbyGuiController;

    public static GUIDTOvisitor guidtovisitor;

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        gameViewController = new FXMLLoader(
                getClass().getResource("/it/polimi/gc06/mesos/fxml/mesos.fxml")
        ).getController();
        boardController = new FXMLLoader(
                getClass().getResource("/it/polimi/gc06/mesos/fxml/board.fxml")
        ).getController();
        lobbyGuiController = new FXMLLoader(
                getClass().getResource("/it/polimi/gc06/mesos/fxml/lobby.fxml")
        ).getController();

        guidtovisitor = new GUIDTOvisitor(boardController, gameViewController, lobbyGuiController);

        mockSmallModel();

        changeScene("/it/polimi/gc06/mesos/fxml/Mesos.fxml");
        primaryStage.show();
    }

    public static void changeScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), WIDTH, HEIGHT);
            scene.getStylesheets().add(Objects.requireNonNull(GUI.class.getResource("/it/polimi/gc06/mesos/css/board_style.css")).toExternalForm());
            primaryStage.setTitle("Mesos");
            javafx.application.Platform.runLater(() -> primaryStage.setMaximized(true));
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mockSmallModel() throws IOException {
        ModelInstancesManager modelInstancesManager = new ModelInstancesManager();
        ArrayList<String> players = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            players.add("Player " + i);
        }
        GameModel gameModel = modelInstancesManager.createGame(players);

        gameModel.startGame();

        HashMap<Player, PlayerView> playersMap = new HashMap<>();

        smallModel = new SmallModel("");

        smallModel.setEra(gameModel.getBoard().getCurrentEra());
        smallModel.setPhase(gameModel.getTurnManager().getPhase().toString());
        smallModel.setRound(gameModel.getTurnManager().getRound());
        smallModel.setActive(gameModel.getTurnManager().getActivePlayer().getNickname().equals("Player 1"));
        smallModel.setCanSkip(false);
        smallModel.setLeaderboard(new ArrayList<>());
        smallModel.setTribeDeckSize(gameModel.getTribeCardsDeck().get(Era.ERA_I).size() + gameModel.getTribeCardsDeck().get(Era.ERA_II).size() + gameModel.getTribeCardsDeck().get(Era.ERA_III).size());

        Player clientPlayer = null;

        for (Player player : gameModel.getPlayers()) {
            if (player.getNickname().equals("Player 1")) {
                clientPlayer = player;
                PlayerView p = new PlayerView(player.getNickname(), player.getPlayerColor());
                playersMap.put(player, p);
                smallModel.setPlayer(player.getNickname(), player.getPlayerColor());
                smallModel.getPlayer().setNumFood(player.getFoodTokens());
                smallModel.getPlayer().setNumPrestige(player.getPrestigeTokens());
            } else {
                PlayerView p = new PlayerView(player.getNickname(), player.getPlayerColor());
                playersMap.put(player, p);
                smallModel.getOpponents().add(p);
            }
        }

        smallModel.setTopDrawNum(clientPlayer.getTopDrawNum());
        smallModel.setBottomDrawNum(clientPlayer.getBottomDrawNum());

        smallModel.getTopRow().addAll(gameModel.getBoard().getTopRow());
        smallModel.getBottomRow().addAll(gameModel.getBoard().getBottomRow());
        smallModel.getTopBuildings().addAll(gameModel.getBoard().getTopBuildings());
        smallModel.getBottomBuildings().addAll(gameModel.getBoard().getBottomBuildings());

        ArrayList<PlayerView> turnOrderTile = new ArrayList<>();
        for (TileSlot slot : gameModel.getBoard().getTurnOrderTile().slots()) {
            turnOrderTile.add(playersMap.get(slot.getPlayer()));
        }

        smallModel.getTurnOrderTile().addAll(turnOrderTile);

        smallModel.getOfferTrack().addAll(gameModel.getBoard().getOfferTrack().stream().map(slot -> {
            if (slot.isEmpty()) {
                TileSlotView slotView = new TileSlotView();
                slotView.setTileEffect(slot.getTileEffect());
                return slotView;
            } else {
                TileSlotView slotView = new TileSlotView();
                slotView.setPlayer(playersMap.get(slot.getPlayer()));
                slotView.setTileEffect(slot.getTileEffect());
                return slotView;
            }
        }).toList());

        imageFetcher = new ImageFetcher(turnOrderTile.size());
    }
}
