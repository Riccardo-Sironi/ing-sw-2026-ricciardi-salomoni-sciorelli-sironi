package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.view.gui.helpers.PhaseOverlays;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
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

    @FXML
    public void initialize() {

        if (overlaysBox != null) {
            initOverlays();
            // showPhaseOverlay();
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

            overlaysBox.setAlignment(Pos.CENTER);

            PhaseOverlays phase = smallModel != null ? PhaseOverlays.getPhase(smallModel.getPhase()) : null;

            if (phase != null) {
                overlay = new ImageView(phase.getOverlayPath());
            }

            // TODO : this is just for now to test the animation
            overlay = new ImageView(new Image("end_of_round_phase_overlay.png"));

            overlay.setPreserveRatio(true);
            overlay.fitHeightProperty().bind(root.heightProperty().multiply(0.8));
            overlay.fitWidthProperty().bind(root.widthProperty().multiply(0.8));

            overlay.setOpacity(0);
            overlay.setVisible(false);

            overlaysBox.getChildren().add(overlay);
            overlaysBox.toFront();
            overlaysBox.setMouseTransparent(true);
        }
    }

    public void showPhaseOverlay() {
        if (overlay == null) return;

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

        fadeOut.setOnFinished(e -> overlay.setVisible(false));

        SequentialTransition sequence = new SequentialTransition(initialStay, fadeIn, scaleIn, scaleOut, fadeOut);
        sequence.play();
    }

    @Override
    public void update(SmallModelEditor dto) {
        dto.accept(guidtovisitor);
    }
}