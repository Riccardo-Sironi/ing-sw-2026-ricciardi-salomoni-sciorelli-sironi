package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class GameViewController {
    @FXML
    public StackPane root;

    @FXML
    public BorderPane mainPane;

    @FXML
    public HBox opponentsContainer;

    @FXML
    public HBox board;

    private SmallModel smallModel;

    public void initialize() {

        if (opponentsContainer != null) {
            opponentsContainer.setVisible(false);
            opponentsContainer.setManaged(false);
        }

        if (board != null) {
            board.prefWidthProperty().bind(mainPane.widthProperty());
            board.prefHeightProperty().bind(mainPane.heightProperty());
        }
    }
}