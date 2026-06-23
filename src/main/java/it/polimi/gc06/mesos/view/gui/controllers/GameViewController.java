package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.view.gui.GameScene;
import it.polimi.gc06.mesos.view.gui.helpers.SoundManager;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import static it.polimi.gc06.mesos.view.gui.GUI.*;

public class GameViewController {

    @FXML
    private StackPane root;

    @FXML
    private HBox board;

    @FXML
    private HBox overlaysBox;

    @FXML
    public HBox endGameBlurBox;

    private ImageView overlay;
    private SequentialTransition overlaySequence;

    /**
     * Initializes the controller, sets up UI bindings, starts the game music,
     * and performs the initial setup for overlays and the game board.
     */
    @FXML
    public void initialize() {

        guidtovisitor.setGameViewController(this);

        SoundManager.getInstance().playGameMusic();

        if (board != null) {
            board.prefWidthProperty().bind(root.widthProperty());
            board.prefHeightProperty().bind(root.heightProperty());
        }

        if (overlaysBox != null) {
            initOverlays();
        }

        if (endGameBlurBox != null) {
            endGameBlurBox.prefWidthProperty().bind(root.widthProperty());
            endGameBlurBox.prefHeightProperty().bind(root.heightProperty());
            endGameBlurBox.setStyle("-fx-background-color: rgb(21, 21, 21);"); // temp could make something fancier
            endGameBlurBox.setEffect(new GaussianBlur());
            endGameBlurBox.setVisible(false);
            endGameBlurBox.setMouseTransparent(true);
        }

        drawEraBackground();

        showEraOverlay(smallModel.getEra(), () -> showPhaseOverlay(smallModel.getPhase(), null));
    }

    /**
     * Configures the overlay container and initializes the animation sequences
     * used for phase and era transitions.
     */
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

        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), overlay);
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

        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), overlay);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        overlaySequence = new SequentialTransition(fadeIn, scaleIn, scaleOut, fadeOut);
    }

    /**
     * Executes the visual animation sequence for an overlay.
     *
     * @param image          The image to display.
     * @param endOfAnimation Runnable to execute after the animation completes.
     */
    private void playOverlay(Image image, Runnable endOfAnimation) {
        if (overlay == null || image == null) return;

        overlaySequence.stop();

        overlay.setImage(image);
        overlay.setVisible(true);
        overlay.setOpacity(0);
        overlay.setScaleX(1.0);
        overlay.setScaleY(1.0);

        overlaysBox.setVisible(true);
        overlaysBox.setMouseTransparent(false);

        overlaySequence.setOnFinished(e -> {
            overlay.setVisible(false);
            overlaysBox.setVisible(false);
            overlaysBox.setMouseTransparent(true);
            if (endOfAnimation != null) endOfAnimation.run();
        });

        overlaySequence.playFromStart();
    }

    /**
     * Triggers the display of the phase transition overlay.
     *
     * @param phase          The identifier of the current phase.
     * @param endOfAnimation Runnable to execute after the animation finishes.
     */
    public void showPhaseOverlay(String phase, Runnable endOfAnimation) {
        if (smallModel.getPhase() == null) return;

        playOverlay(imageFetcher.getPhaseOverlayImage(phase), endOfAnimation);
    }

    /**
     * Triggers the display of the era transition overlay.
     *
     * @param era            The current era enum value.
     * @param endOfAnimation Runnable to execute after the animation finishes.
     */
    public void showEraOverlay(Era era, Runnable endOfAnimation) {
        if (smallModel.getEra() == null) return;

        playOverlay(imageFetcher.getEraOverlayImage(era), endOfAnimation);
    }

    /**
     * Updates the root container's background style based on the current era
     * retrieved from the model.
     */
    public void drawEraBackground() {
        String backgroundUrl = imageFetcher.getEraBackgroundsImage(smallModel.getEra());

        if (backgroundUrl == null) {
            System.err.println("Unable to load the background for Era " + smallModel.getEra());
            return;
        }

        String style = "-fx-padding: 0px;" +
                "-fx-background-image: url('" + backgroundUrl + "');" +
                "-fx-background-size: cover;" +
                "-fx-background-position: center;" +
                "-fx-background-repeat: no-repeat;";

        root.setStyle(style);
    }

    /**
     * Navigates the application to the leaderboard scene.
     */
    public void handleLeaderboardChange() {
        changeScene(GameScene.LEADERBOARD.getPath());
    }

    /**
     * Handles the end-game visual transition by showing a blurred overlay.
     *
     * @param endOfAnimation Runnable to execute after the fade-in animation.
     */
    public void handleEndGame(Runnable endOfAnimation) {

        endGameBlurBox.setVisible(true);
        endGameBlurBox.setMouseTransparent(false);
        endGameBlurBox.setOpacity(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), endGameBlurBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeIn.setOnFinished(e -> {
            if (endOfAnimation != null) endOfAnimation.run();
        });

        fadeIn.play();
    }
}