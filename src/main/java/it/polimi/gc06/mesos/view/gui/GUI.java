package it.polimi.gc06.mesos.view.gui;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.InstancesManager.ModelInstancesManager;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.network.client.Client;
import it.polimi.gc06.mesos.view.View;
import it.polimi.gc06.mesos.view.gui.controllers.*;
import it.polimi.gc06.mesos.view.gui.visitors.GUIDTOvisitor;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import it.polimi.gc06.mesos.view.smallModel.TileSlotView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class GUI extends Application implements View, ModelListener {

    public static int WIDTH = 1920;
    public static int HEIGHT = 1080;

    public static SmallModel smallModel;
    public static ImageFetcher imageFetcher;
    public static Client client;

    public static GUIDTOvisitor guidtovisitor;

    public static Stage primaryStage;

    private static GUI gui;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        gui = this;
        guidtovisitor = new GUIDTOvisitor();

        changeScene(GameScene.START.getPath());
        
        primaryStage.show();
    }

    public static void changeScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource(fxmlPath));
            Parent newRoot = loader.load();

            String css = Objects.requireNonNull(GUI.class.getResource("/it/polimi/gc06/mesos/css/board_style.css")).toExternalForm();

            Platform.runLater(() -> {
                if (primaryStage.getScene() == null) {
                    Scene scene = new Scene(newRoot, WIDTH, HEIGHT);
                    scene.getStylesheets().add(css);
                    primaryStage.setTitle("Mesos");
                    primaryStage.setScene(scene);
                    primaryStage.setMaximized(true);
                } else {
                    Scene scene = primaryStage.getScene();

                    double currentWidth = scene.getWidth();
                    double currentHeight = scene.getHeight();

                    newRoot.getStylesheets().add(css);

                    scene.setRoot(newRoot);

                    if (newRoot instanceof javafx.scene.layout.Region) {
                        ((javafx.scene.layout.Region) newRoot).setPrefSize(currentWidth, currentHeight);
                    }

                    newRoot.applyCss();
                    newRoot.layout();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void subscribeGUI() {
        try {
            client.getServerConnection().subscribe(gui);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(SmallModelEditor dto) {
        System.out.println("Received update request");
        Platform.runLater(() -> {
            dto.accept(guidtovisitor);
        });
    }
}
