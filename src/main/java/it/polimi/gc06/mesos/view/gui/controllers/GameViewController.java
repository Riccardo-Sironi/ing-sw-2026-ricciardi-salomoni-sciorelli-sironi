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
    public BorderPane board;

    @FXML
    public HBox playerInventory;

    private SmallModel smallModel;

    private static final double CENTER = 0.75;
    private static final double BOTTOM = 0.18;
    private static double TOP = 1 - CENTER - BOTTOM;

    public void initialize() {
        opponentsContainer.prefWidthProperty().bind(mainPane.widthProperty());
        opponentsContainer
                .prefHeightProperty()
                .bind(mainPane.heightProperty().multiply(TOP));

        board.prefWidthProperty().bind(mainPane.widthProperty());
        board
                .prefHeightProperty()
                .bind(mainPane.heightProperty().multiply(CENTER));

        playerInventory.prefWidthProperty().bind(mainPane.widthProperty());
        playerInventory
                .prefHeightProperty()
                .bind(mainPane.heightProperty().multiply(BOTTOM));
    }
}
