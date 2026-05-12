package it.polimi.gc06.mesos.view.gui;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.characters.ShamanCard;
import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
import it.polimi.gc06.mesos.view.View;
import it.polimi.gc06.mesos.view.gui.controllers.BoardController;
import it.polimi.gc06.mesos.view.gui.controllers.GameViewController;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class GUI extends Application implements View {

    public static int WIDTH = 1920;
    public static int HEIGHT = 1080;

    public static SmallModel smallModel;
    public static ImageFetcher imageFetcher;

    public static GameViewController gameViewController;
    public static BoardController boardController;

    @Override
    public void start(Stage stage) throws IOException {
        mockSmallModel();

        FXMLLoader gameViewLoader = new FXMLLoader(
                getClass().getResource("/it/polimi/gc06/mesos/fxml/mesos.fxml")
        );
        Scene scene = new Scene(gameViewLoader.load(), WIDTH, HEIGHT);

        scene.getStylesheets().add(getClass().getResource("/it/polimi/gc06/mesos/css/board_style.css").toExternalForm());


        // smallModel.setListener(gameViewLoader.getController());

        stage.setTitle("Mesos");
        stage.setScene(scene);

        stage.show();
        javafx.application.Platform.runLater(() -> stage.setMaximized(true));
    }

    private void mockSmallModel() throws IOException {
//        ModelInstancesManager modelInstancesManager = new ModelInstancesManager();
//        ArrayList<String> playerNames = new ArrayList<>();
//        playerNames.add("Player 1");
//        playerNames.add("Player 2");
//        playerNames.add("Player 3");
//        playerNames.add("Player 4");
//        playerNames.add("Player 5");
//
//        GameModel model = modelInstancesManager.createGame(playerNames);

        smallModel = new SmallModel("");

        smallModel.setEra(Era.ERA_I);
        smallModel.setPhase(new PlacingTotemPhase().toString());
        smallModel.setRound(0);
        smallModel.setActive(true);
        smallModel.setCanSkip(false);
        smallModel.setTribeDeckSize(80);

        smallModel.setPlayer("Player 1", Color.BLUE);
        PlayerView clientPlayer = smallModel.getPlayer();
        clientPlayer.setNumFood(5);
        clientPlayer.setNumPrestige(10);
//        for (int i = 0; i < 5; i++) {
//            clientPlayer.getCharacters().add(new ShamanCard(Era.ERA_I, 1));
//        }

        smallModel.getOpponents().add(new PlayerView("Player 2", Color.RED));
        smallModel.getOpponents().add(new PlayerView("Player 3", Color.WHITE));
        smallModel.getOpponents().add(new PlayerView("Player 4", Color.YELLOW));
        smallModel.getOpponents().add(new PlayerView("Player 5", Color.PURPLE));

//        for (int i = 0; i < 12; i++) {
//            smallModel.getTopRow().add(new ShamanCard(Era.ERA_I, 1));
//            smallModel.getBottomRow().add(new ShamanCard(Era.ERA_I, 2));
//        }

        ArrayList<PlayerView> playerViews = new ArrayList<>();
        playerViews.addAll(smallModel.getOpponents());
        playerViews.add(clientPlayer);

        smallModel.getTurnOrderTile().addAll(playerViews);

        imageFetcher = new ImageFetcher(playerViews.size());
    }
}
