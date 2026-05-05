package it.polimi.gc06.mesos.view.gui;

import it.polimi.gc06.mesos.view.SmallModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application {
    public static int WIDTH = 1920;
    public static int HEIGHT = 1080;

    //static SmallModel smallModel = new SmallModel();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader gameViewLoader = new FXMLLoader(getClass().getResource("/it/polimi/gc06/mesos/fxml/Mesos.fxml"));
        Scene scene = new Scene(gameViewLoader.load(), WIDTH, HEIGHT);

        scene.getStylesheets().add(getClass().getResource("/it/polimi/gc06/mesos/css/board_style.css").toExternalForm());

        // smallModel.setListener(gameViewLoader.getController());

        stage.setTitle("Mesos");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
