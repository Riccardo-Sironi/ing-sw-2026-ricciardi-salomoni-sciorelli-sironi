package it.polimi.gc06.mesos.view.gui;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.characters.ArtistCard;
import it.polimi.gc06.mesos.model.cards.characters.GathererCard;
import it.polimi.gc06.mesos.model.cards.characters.HunterCard;
import it.polimi.gc06.mesos.model.cards.characters.ShamanCard;
import it.polimi.gc06.mesos.model.gameBoard.ChooseCardTileEffect;
import it.polimi.gc06.mesos.model.gameBoard.FoodTileEffect;
import it.polimi.gc06.mesos.model.gameTurnManager.OfferResolutionPhase;
import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
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

    @Override
    public void start(Stage stage) throws IOException {
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

        FXMLLoader gameViewLoader = new FXMLLoader(
                getClass().getResource("/it/polimi/gc06/mesos/fxml/Mesos.fxml")
        );
        Scene scene = new Scene(gameViewLoader.load(), WIDTH, HEIGHT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/it/polimi/gc06/mesos/css/board_style.css")).toExternalForm());

        // ModelListener controller = gameViewLoader.getController();
        // smallModel.setListener(controller);

        stage.setTitle("Mesos");
        stage.setScene(scene);
        javafx.application.Platform.runLater(() -> stage.setMaximized(true));
        stage.show();
    }

    private void mockSmallModel() throws IOException {
        smallModel = new SmallModel("");

        smallModel.setEra(Era.ERA_I);
        smallModel.setPhase(new PlacingTotemPhase().toString());
        smallModel.setRound(0);
        smallModel.setActive(true);
        smallModel.setCanSkip(false);
        smallModel.setLeaderboard(new ArrayList<>());
        smallModel.setTribeDeckSize(80);

        smallModel.setTopDrawNum(1);
        smallModel.setBottomDrawNum(0);

        smallModel.setPlayer("Player 1", Color.RED);
        PlayerView clientPlayer = smallModel.getPlayer();
        clientPlayer.setNumFood(5);
        clientPlayer.setNumPrestige(10);

        clientPlayer.getCharacters().add(new ShamanCard(Era.ERA_I, 1));
        clientPlayer.getCharacters().add(new ArtistCard(Era.ERA_I));
        clientPlayer.getCharacters().add(new ShamanCard(Era.ERA_I, 3));
        clientPlayer.getCharacters().add(new GathererCard(Era.ERA_I));
        clientPlayer.getCharacters().add(new HunterCard(Era.ERA_I, false));
        clientPlayer.getCharacters().add(new ShamanCard(Era.ERA_I, 2));
        clientPlayer.getCharacters().add(new ArtistCard(Era.ERA_I));
        clientPlayer.getCharacters().add(new HunterCard(Era.ERA_I, true));


        PlayerView p2 = new PlayerView("Player 2", Color.BLUE);
        p2.setNumFood(5);
        p2.getCharacters().add(new ShamanCard(Era.ERA_I, 3));
        p2.getCharacters().add(new ShamanCard(Era.ERA_I, 3));
        p2.getCharacters().add(new ShamanCard(Era.ERA_I, 3));
        p2.getCharacters().add(new ShamanCard(Era.ERA_I, 3));
        PlayerView p3 = new PlayerView("Player 3", Color.WHITE);
        p3.setNumPrestige(10);
        PlayerView p4 = new PlayerView("Player 4", Color.PURPLE);
        p4.setNumFood(100);
        p4.getCharacters().add(new ShamanCard(Era.ERA_I, 1));
        p4.getCharacters().add(new ShamanCard(Era.ERA_I, 1));
        p4.setNumPrestige(100);
        PlayerView p5 = new PlayerView("Player 5", Color.YELLOW);

        smallModel.getOpponents().add(p2);
        smallModel.getOpponents().add(p3);
        smallModel.getOpponents().add(p4);
        smallModel.getOpponents().add(p5);

        smallModel.getTopRow().add(new ShamanCard(Era.ERA_I, 1));
        smallModel.getTopRow().add(new ArtistCard(Era.ERA_I));
        smallModel.getTopRow().add(new ShamanCard(Era.ERA_I, 3));
        smallModel.getTopRow().add(new GathererCard(Era.ERA_I));
        smallModel.getTopRow().add(new HunterCard(Era.ERA_I, false));
        smallModel.getTopRow().add(new ShamanCard(Era.ERA_I, 2));
        smallModel.getTopRow().add(new ArtistCard(Era.ERA_I));
        smallModel.getTopRow().add(new HunterCard(Era.ERA_I, true));

        smallModel.getBottomRow().add(new ShamanCard(Era.ERA_I, 1));
        smallModel.getBottomRow().add(new ArtistCard(Era.ERA_I));
        smallModel.getBottomRow().add(new ShamanCard(Era.ERA_I, 3));
        smallModel.getBottomRow().add(new GathererCard(Era.ERA_I));
        smallModel.getBottomRow().add(new HunterCard(Era.ERA_I, false));
        smallModel.getBottomRow().add(new ShamanCard(Era.ERA_I, 2));
        smallModel.getBottomRow().add(new ArtistCard(Era.ERA_I));
        smallModel.getBottomRow().add(new HunterCard(Era.ERA_I, true));

        ArrayList<PlayerView> turnOrderTile = new ArrayList<>();
        turnOrderTile.add(smallModel.getOpponents().get(0));
        turnOrderTile.add(smallModel.getOpponents().get(1));
        turnOrderTile.add(clientPlayer);
        turnOrderTile.add(null);
        turnOrderTile.add(null);

        smallModel.getTurnOrderTile().addAll(turnOrderTile);

        TileSlotView tA = new TileSlotView();
        tA.setTileEffect(new FoodTileEffect(3));
        smallModel.getOfferTrack().add(tA);

        TileSlotView tB = new TileSlotView();
        tB.setTileEffect(new ChooseCardTileEffect(0, 1));
        tB.setPlayer(smallModel.getOpponents().get(2));
        smallModel.getOfferTrack().add(tB);

        TileSlotView tC = new TileSlotView();
        tC.setTileEffect(new ChooseCardTileEffect(1, 0));
        smallModel.getOfferTrack().add(tC);

        TileSlotView tD = new TileSlotView();
        tD.setTileEffect(new ChooseCardTileEffect(0, 2));
        smallModel.getOfferTrack().add(tD);

        TileSlotView tE = new TileSlotView();
        tE.setTileEffect(new ChooseCardTileEffect(1, 1));
        tE.setPlayer(smallModel.getOpponents().get(3));
        smallModel.getOfferTrack().add(tE);

        TileSlotView tF = new TileSlotView();
        tF.setTileEffect(new ChooseCardTileEffect(2, 0));
        smallModel.getOfferTrack().add(tF);

        TileSlotView tG = new TileSlotView();
        tG.setTileEffect(new ChooseCardTileEffect(2, 1));
        smallModel.getOfferTrack().add(tG);

        imageFetcher = new ImageFetcher(turnOrderTile.size());
    }
}
