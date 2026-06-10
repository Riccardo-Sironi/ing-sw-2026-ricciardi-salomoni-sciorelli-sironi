package it.polimi.gc06.mesos.view.gui.controllers;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class StartController {

    @FXML
    private StackPane rootPane;
    @FXML
    private ImageView backgroundImageView;
    @FXML
    private ImageView flamesImageView;
    @FXML
    private ImageView particlesImageView;

    @FXML
    private Region bottomGlow;
    @FXML
    private Region topVignette;
    @FXML
    private Region leftVignette;
    @FXML
    private Region rightVignette;

    @FXML
    private Label titleLabel;
    @FXML
    private Label pressKeyLabel;

    private boolean transitionStarted = false;
    private FadeTransition glowPulse;

    private static final double TIME_FADE_PARTICLES = 500;
    private static final double TIME_FADE_TEXTS = 1500;
    private static final double TIME_SHRINK_FLAMES = 3000;
    private static final double TIME_FADE_FLAMES = 1500;
    private static final double TIME_FADE_BACKGROUND = 2000;
    private static final double TIME_WAIT_IN_DARK = 200;
    private static final double TIME_FADE_IN_LOGIN = 500;

    @FXML
    public void initialize() {
        loadImage(backgroundImageView, "/imgs/background/mesos.png");
        loadImage(flamesImageView, "/imgs/effect/flames.gif");
        loadImage(particlesImageView, "/imgs/effect/fire_particles.gif");
        loadFonts();

        setupDynamicLayout();
        setupPulseAnimations();

        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnMouseClicked(this::handleStart);
                newScene.setOnKeyPressed(this::handleStart);
                rootPane.requestFocus();
            }
        });
    }

    private void setupDynamicLayout() {
        backgroundImageView.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(rootPane.heightProperty());
        backgroundImageView.setPreserveRatio(false);

        flamesImageView.setPreserveRatio(true);
        flamesImageView.fitWidthProperty().bind(rootPane.widthProperty().multiply(0.57));
        flamesImageView.translateYProperty().bind(rootPane.heightProperty().multiply(0.3));

        particlesImageView.setPreserveRatio(true);
        particlesImageView.fitWidthProperty().bind(rootPane.widthProperty().multiply(0.78));

        NumberBinding scale = Bindings.min(
                rootPane.widthProperty().divide(1920.0),
                rootPane.heightProperty().divide(1080.0)
        );

        titleLabel.scaleXProperty().bind(scale);
        titleLabel.scaleYProperty().bind(scale);
        titleLabel.translateYProperty().bind(rootPane.heightProperty().multiply(-0.046));

        pressKeyLabel.scaleXProperty().bind(scale);
        pressKeyLabel.scaleYProperty().bind(scale);
        pressKeyLabel.translateYProperty().bind(rootPane.heightProperty().multiply(0.07));
    }

    private void setupPulseAnimations() {
        createPulse(pressKeyLabel, 2.0, 0.7, 0.0);
        glowPulse = createPulse(bottomGlow, 0.25, 0.3, 0.9);
        createPulse(topVignette, 0.4, 0.9, 0.6);
        createPulse(leftVignette, 0.7, 0.9, 0.75);
        createPulse(rightVignette, 0.6, 0.9, 0.8);
    }

    private FadeTransition createPulse(Node node, double durationSeconds, double fromValue, double toValue) {
        if (node == null) return null;
        FadeTransition pulse = new FadeTransition(Duration.seconds(durationSeconds), node);
        pulse.setFromValue(fromValue);
        pulse.setToValue(toValue);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
        return pulse;
    }

    private void loadImage(ImageView imageView, String path) {
        URL url = getClass().getResource(path);
        if (url != null) imageView.setImage(new Image(url.toExternalForm()));
    }

    private void loadFonts() {
        try {
            Font titleFont = Font.loadFont(getClass().getResourceAsStream("/it/polimi/gc06/mesos/fonts/Cave-Stone.ttf"), 200);
            Font subtitleFont = Font.loadFont(getClass().getResourceAsStream("/it/polimi/gc06/mesos/fonts/ArcadianG.ttf"), 35);

            if (titleFont != null) titleLabel.setFont(titleFont);
            if (subtitleFont != null) pressKeyLabel.setFont(subtitleFont);

        } catch (Exception e) {
            System.err.println("Error: Loading Fonts failed");
            //e.printStackTrace();
        }
    }

    @FXML
    public void handleStart(Event event) {
        if (transitionStarted) return;

        transitionStarted = true;
        rootPane.setDisable(true);

        System.out.println("Start game");

        try {
            URL loginUrl = getClass().getResource("/it/polimi/gc06/mesos/fxml/Login.fxml");
            if (loginUrl == null) {
                System.err.println("Error: Login.fxml hasn't been found");
                return;
            }

            Parent loginRoot = new FXMLLoader(loginUrl).load();

            if (rootPane.getScene() != null) {
                rootPane.getScene().setFill(Color.BLACK);
            }

            if (pressKeyLabel != null) pressKeyLabel.setVisible(false);
            if (glowPulse != null) glowPulse.stop();

            ParallelTransition shutdown = buildShutdownAnimation();

            shutdown.setOnFinished(e -> swapSceneAndFadeIn(loginRoot));
            shutdown.play();

        } catch (IOException e) {
            System.err.println("Error I/O in loading Login.fxml");
            //e.printStackTrace();
        }
    }

    private ParallelTransition buildShutdownAnimation() {
        ParallelTransition shutdown = new ParallelTransition();

        if (flamesImageView != null) {
            flamesImageView.translateYProperty().unbind();

            TranslateTransition moveFlames = new TranslateTransition(Duration.millis(TIME_SHRINK_FLAMES), flamesImageView);
            moveFlames.setByY(1000);

            FadeTransition fadeFlames = new FadeTransition(Duration.millis(TIME_FADE_FLAMES), flamesImageView);
            fadeFlames.setToValue(0.5);

            shutdown.getChildren().addAll(moveFlames, fadeFlames);
        }

        addFadeToShutdown(shutdown, particlesImageView, TIME_FADE_PARTICLES, 0.0);
        addFadeToShutdown(shutdown, titleLabel, TIME_FADE_TEXTS, 0.0);
        addFadeToShutdown(shutdown, bottomGlow, TIME_FADE_PARTICLES, 0.0);
        addFadeToShutdown(shutdown, backgroundImageView, TIME_FADE_BACKGROUND, 0.0);

        return shutdown;
    }

    private void addFadeToShutdown(ParallelTransition pt, Node node, double durationMillis, double toValue) {
        if (node != null) {
            FadeTransition fade = new FadeTransition(Duration.millis(durationMillis), node);
            fade.setToValue(toValue);
            pt.getChildren().add(fade);
        }
    }

    private void swapSceneAndFadeIn(Parent newRoot) {
        Stage stage = (Stage) rootPane.getScene().getWindow();

        double currentWidth = stage.getScene().getWidth();
        double currentHeight = stage.getScene().getHeight();

        stage.getScene().setFill(Color.BLACK);
        stage.getScene().setRoot(newRoot);

        if (newRoot instanceof Region) {
            ((Region) newRoot).setPrefSize(currentWidth, currentHeight);
        }

        newRoot.applyCss();
        newRoot.layout();
        newRoot.setOpacity(0.0);

        PauseTransition pauseTransition = new PauseTransition(Duration.millis(TIME_WAIT_IN_DARK));
        pauseTransition.setOnFinished(event -> {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(TIME_FADE_IN_LOGIN), newRoot);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        pauseTransition.play();
    }
}