package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.view.gui.helpers.PhaseOverlays;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import static it.polimi.gc06.mesos.view.gui.GUI.guidtovisitor;
import static it.polimi.gc06.mesos.view.gui.GUI.smallModel;

public class GameViewController implements ModelListener {
    @FXML
    public StackPane root;

    @FXML
    public HBox board;

    @FXML
    public HBox overlaysBox;

    public ImageView overlay;

    private SequentialTransition overlaySequence;

    @FXML
    public void initialize() {

        if (overlaysBox != null) {
            initOverlays();
        }

        if (board != null) {
            board.prefWidthProperty().bind(root.widthProperty());
            board.prefHeightProperty().bind(root.heightProperty());
        }
    }

    private void initOverlays() {
        if (overlaysBox != null) {
            overlaysBox.prefWidthProperty().bind(root.widthProperty());
            overlaysBox.prefHeightProperty().bind(root.heightProperty());
            overlaysBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
            overlaysBox.setMouseTransparent(false);

            overlaysBox.setAlignment(Pos.CENTER);

            PhaseOverlays phase = smallModel != null ? PhaseOverlays.getPhase(smallModel.getPhase()) : null;

            if (phase != null) {
                overlay = new ImageView(phase.getOverlayPath());
            }

            overlay = new ImageView();

            overlay.setPreserveRatio(true);
            overlay.fitHeightProperty().bind(root.heightProperty().multiply(0.8));
            overlay.fitWidthProperty().bind(root.widthProperty().multiply(0.8));

            overlay.setOpacity(0);
            overlay.setVisible(false);

            overlaysBox.getChildren().add(overlay);
            overlaysBox.toFront();

            PauseTransition initialStay = new PauseTransition(Duration.seconds(0.5));

            overlay.setOpacity(0);
            overlay.setVisible(true);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), overlay);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(1.2), overlay);
            scaleIn.setFromX(1.0);
            scaleIn.setFromY(1.0);
            scaleIn.setToX(1.1);
            scaleIn.setToY(1.1);

            ScaleTransition scaleOut = new ScaleTransition(Duration.seconds(0.8), overlay);
            scaleOut.setFromX(1.1);
            scaleOut.setFromY(1.1);
            scaleOut.setToX(1.08);
            scaleOut.setToY(1.08);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), overlay);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                overlay.setVisible(false);
                overlaysBox.setVisible(false);
            });

            overlaySequence = new SequentialTransition(initialStay, fadeIn, scaleIn, scaleOut, fadeOut);
        }
    }

    public void showPhaseOverlay() {
        if (overlay == null) return;

        overlay.setImage(new Image(PhaseOverlays.getPhase(smallModel.getPhase()).getOverlayPath()));

        overlaySequence.play();
    }

    public void showEraOverlay() {
        if (overlay == null) return;

        overlay.setImage(new Image(smallModel.getEra().toString().toLowerCase() + "_overlay.png"));

        overlaySequence.play();
    }

    @Override
    public void update(SmallModelEditor dto) {
        dto.accept(guidtovisitor);
    }
}