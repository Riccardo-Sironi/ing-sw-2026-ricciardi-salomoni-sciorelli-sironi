package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class GameViewController {
    @FXML
    public BorderPane mainPane;
    @FXML
    public HBox opponentsContainer;
    @FXML
    public BorderPane board;
    @FXML
    public HBox playerInventory;


    private SmallModel smallModel;

    public void initialize() {
        switchSetup(true);
    }

    private void switchSetup(boolean setup) {
        double center;
        double top;
        double bottom;

        if (setup) {
            center = 0.75;
            bottom = 0.18;
            top = 1 - center - bottom;
        } else {
            center = 1;
            bottom = 0;
            top = 0;
        }

        opponentsContainer.prefWidthProperty().bind(mainPane.widthProperty());
        opponentsContainer.prefHeightProperty().bind(mainPane.heightProperty().multiply(top));

        board.prefWidthProperty().bind(mainPane.widthProperty());
        board.prefHeightProperty().bind(mainPane.heightProperty().multiply(center));

        playerInventory.prefWidthProperty().bind(mainPane.widthProperty());
        playerInventory.prefHeightProperty().bind(mainPane.heightProperty().multiply(bottom));
    }
}
