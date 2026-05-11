package it.polimi.gc06.mesos.view.gui;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI extends Application {

    public static int WIDTH = 1920;
    public static int HEIGHT = 1080;

    public static SmallModel smallModel;
    public static ImageFetcher imageFetcher;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader gameViewLoader = new FXMLLoader(
            getClass().getResource("/it/polimi/gc06/mesos/fxml/Mesos.fxml")
        );
        Scene scene = new Scene(gameViewLoader.load(), WIDTH, HEIGHT);

        scene
            .getStylesheets()
            .add(
                getClass()
                    .getResource("/it/polimi/gc06/mesos/css/board_style.css")
                    .toExternalForm()
            );

        // smallModel.setListener(gameViewLoader.getController());

        stage.setTitle("Mesos");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        javafx.application.Platform.runLater(() -> stage.setMaximized(true));
    }
}
