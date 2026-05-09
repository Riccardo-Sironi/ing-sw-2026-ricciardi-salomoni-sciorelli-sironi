package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GameViewController {
    @FXML
    public StackPane root;

    @FXML
    public BorderPane mainPane;

    @FXML
    public HBox opponentsContainer; // Top

    @FXML
    public VBox board;

    @FXML
    public HBox playerInventory;

    private SmallModel smallModel;

    public void initialize() {
        if (opponentsContainer != null) {
            opponentsContainer.setVisible(false);
            opponentsContainer.setManaged(false);
        }

        if (playerInventory != null) {
            playerInventory.setVisible(false);
            playerInventory.setManaged(false);
        }

        if (board != null) {
            board.prefWidthProperty().bind(mainPane.widthProperty());
            board.prefHeightProperty().bind(mainPane.heightProperty());
        }
    }
}