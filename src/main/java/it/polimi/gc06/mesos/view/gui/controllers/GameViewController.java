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
    public HBox opponentsContainer; // Top (Vecchio, ora lo nascondiamo)

    // IL FIX È QUI: Cambiato da VBox a HBox!
    @FXML
    public HBox board;              // Center (La nostra nuova BoardRoot a due colonne)

    @FXML
    public HBox playerInventory;    // Bottom (Vecchio, ora lo nascondiamo)

    private SmallModel smallModel;

    public void initialize() {
        // 1. Nascondiamo il vecchio contenitore in alto
        if (opponentsContainer != null) {
            opponentsContainer.setVisible(false);
            opponentsContainer.setManaged(false);
        }

        // 2. Nascondiamo il vecchio inventario in basso
        if (playerInventory != null) {
            playerInventory.setVisible(false);
            playerInventory.setManaged(false);
        }

        // 3. Facciamo in modo che la nuova Board occupi il 100% dello spazio del mainPane
        if (board != null) {
            board.prefWidthProperty().bind(mainPane.widthProperty());
            board.prefHeightProperty().bind(mainPane.heightProperty());
        }
    }
}