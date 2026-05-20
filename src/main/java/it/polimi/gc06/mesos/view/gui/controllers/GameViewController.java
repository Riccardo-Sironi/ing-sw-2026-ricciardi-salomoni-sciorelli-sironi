package it.polimi.gc06.mesos.view.gui.controllers;

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

public class GameViewController {

    @FXML
    private StackPane root;

    @FXML
    private HBox board;

    @FXML
    private HBox overlaysBox;

    @FXML
    private BoardController boardController;

    public BoardController getBoardController() {
        return boardController;
    }

    private ImageView overlay;
    private SequentialTransition overlaySequence;

    @FXML
    public void initialize() {

        guidtovisitor.setGameViewController(this);

        if (board != null) {
            board.prefWidthProperty().bind(root.widthProperty());
            board.prefHeightProperty().bind(root.heightProperty());
        }

        if (overlaysBox != null) {
            initOverlays();
        }
    }

    private void initOverlays() {
        overlaysBox.prefWidthProperty().bind(root.widthProperty());
        overlaysBox.prefHeightProperty().bind(root.heightProperty());
        overlaysBox.setAlignment(Pos.CENTER);
        overlaysBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
        overlaysBox.setVisible(false);
        overlaysBox.setMouseTransparent(true);
        overlaysBox.toFront();

        overlay = new ImageView();
        overlay.setPreserveRatio(true);
        overlay.fitWidthProperty().bind(root.widthProperty().multiply(0.8));
        overlay.fitHeightProperty().bind(root.heightProperty().multiply(0.8));
        overlay.setVisible(false);
        overlay.setOpacity(0);
        overlay.setScaleX(1.0);
        overlay.setScaleY(1.0);

        overlaysBox.getChildren().add(overlay);

        PauseTransition initialStay = new PauseTransition(Duration.millis(500));

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), overlay);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(1200), overlay);
        scaleIn.setFromX(1.0);
        scaleIn.setFromY(1.0);
        scaleIn.setToX(1.1);
        scaleIn.setToY(1.1);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(800), overlay);
        scaleOut.setFromX(1.1);
        scaleOut.setFromY(1.1);
        scaleOut.setToX(1.08);
        scaleOut.setToY(1.08);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), overlay);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        overlaySequence = new SequentialTransition(initialStay, fadeIn, scaleIn, scaleOut, fadeOut);

        overlaySequence.setOnFinished(e -> {
            overlay.setVisible(false);
            overlaysBox.setVisible(false);
            overlaysBox.setMouseTransparent(true);
        });
    }

    private void playOverlay(Image image) {
        if (overlay == null || image == null) return;

        overlaySequence.stop();

        overlay.setImage(image);
        overlay.setVisible(true);
        overlay.setOpacity(0);
        overlay.setScaleX(1.0);
        overlay.setScaleY(1.0);

        overlaysBox.setVisible(true);
        overlaysBox.setMouseTransparent(false);

        overlaySequence.playFromStart();
    }

    public void showPhaseOverlay() {
        if (smallModel.getPhase() == null) return;

        String path = "/imgs/overlay/" + smallModel.getPhase().toLowerCase() + "_phase_overlay.png";

        java.io.InputStream imageStream = getClass().getResourceAsStream(path);
        if (imageStream != null) {
            playOverlay(new Image(imageStream));
        } else {
            System.err.println("ERROR: Phase overlay image not found at path: " + path);
        }
    }

    public void showEraOverlay() {
        if (smallModel.getEra() == null) return;

        String path = "/imgs/overlay/" + smallModel.getEra().name().toLowerCase() + "_overlay.png";

        java.io.InputStream imageStream = getClass().getResourceAsStream(path);
        if (imageStream != null) {
            playOverlay(new Image(imageStream));
        } else {
            System.err.println("ERROR: Era overlay image not found at path: " + path);
        }
    }

    public void handleRoundChanged() {
        showPhaseOverlay();
    }

    public void handlePhaseChanged() {
        showPhaseOverlay();
    }
}