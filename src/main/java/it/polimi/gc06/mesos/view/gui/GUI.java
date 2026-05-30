package it.polimi.gc06.mesos.view.gui;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.network.client.Client;
import it.polimi.gc06.mesos.view.View;
import it.polimi.gc06.mesos.view.gui.visitors.GUIDTOvisitor;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
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

    public static GuiEventsManager guiEventsManager;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        gui = this;
        guidtovisitor = new GUIDTOvisitor();
        guiEventsManager = new GuiEventsManager();

        changeScene(GameScene.START.getPath());

        //primaryStage.show();
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

                    primaryStage.show();
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
        client.subscribe(gui);
    }

    @Override
    public void update(SmallModelEditor dto) {
        Platform.runLater(() -> dto.accept(guidtovisitor));
    }
}
