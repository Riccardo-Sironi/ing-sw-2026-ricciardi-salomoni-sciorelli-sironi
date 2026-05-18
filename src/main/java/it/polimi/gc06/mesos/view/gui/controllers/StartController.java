package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.view.gui.GUI;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    @FXML
    private ImageView particlesImageView;

    private boolean transitionStarted = false;
    private FadeTransition glowPulse;

    @FXML
    public void initialize() {

        loadImage(backgroundImageView, "/imgs/background/mesos.png", "background");
        loadImage(flamesImageView, "/imgs/effect/flames.gif", "flames");
        loadImage(particlesImageView, "/imgs/effect/fire_particles.gif", "particles");
        loadFonts();

        setupDynamicLayout();

        FadeTransition pressPulse = new FadeTransition(Duration.seconds(1.5), pressKeyLabel);
        pressPulse.setFromValue(0.8);
        pressPulse.setToValue(0.0);
        pressPulse.setCycleCount(Animation.INDEFINITE);
        pressPulse.setAutoReverse(true);
        pressPulse.play();

        if (bottomGlow != null) {
            glowPulse = new FadeTransition(Duration.seconds(0.25), bottomGlow);
            glowPulse.setFromValue(0.3);
            glowPulse.setToValue(0.9);
            glowPulse.setCycleCount(Animation.INDEFINITE);
            glowPulse.setAutoReverse(true);
            glowPulse.play();
        }

        FadeTransition topPulse = new FadeTransition(Duration.seconds(0.4), topVignette);
        topPulse.setFromValue(0.9);
        topPulse.setToValue(0.6);
        topPulse.setCycleCount(Animation.INDEFINITE);
        topPulse.setAutoReverse(true);
        topPulse.play();

        FadeTransition leftPulse = new FadeTransition(Duration.seconds(0.7), leftVignette);
        leftPulse.setFromValue(0.9);
        leftPulse.setToValue(0.75);
        leftPulse.setCycleCount(Animation.INDEFINITE);
        leftPulse.setAutoReverse(true);
        leftPulse.play();

        FadeTransition rightPulse = new FadeTransition(Duration.seconds(0.6), rightVignette);
        rightPulse.setFromValue(0.9);
        rightPulse.setToValue(0.8);
        rightPulse.setCycleCount(Animation.INDEFINITE);
        rightPulse.setAutoReverse(true);
        rightPulse.play();

        Platform.runLater(() -> {
            rootPane.requestFocus();

            Scene currentScene = rootPane.getScene();
            if (currentScene != null) {
                currentScene.setOnMouseClicked(this::handleStart);
                currentScene.setOnKeyPressed(this::handleStart);
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

    private void loadImage(ImageView imageView, String path, String name) {
        URL url = getClass().getResource(path);
        if (url == null) return;
        imageView.setImage(new Image(url.toExternalForm()));
    }

    private void loadFonts() {
        try {
            Font titleFont = Font.loadFont(getClass().getResourceAsStream("/it/polimi/gc06/mesos/fonts/Cave-Stone.ttf"), 200);
            Font subtitleFont = Font.loadFont(getClass().getResourceAsStream("/it/polimi/gc06/mesos/fonts/KidKnowledge.otf"), 45);

            if (titleFont != null) titleLabel.setFont(titleFont);
            if (subtitleFont != null) pressKeyLabel.setFont(subtitleFont);

        } catch (Exception e) {
            System.err.println("Error: Loading Fonts failed");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleStart(Event event) {

        if (transitionStarted) {
            return;
        }
        transitionStarted = true;
        rootPane.setDisable(true);

        System.out.println("Start game");

        // TIMERS (milliseconds)
        double timeFadeParticles = 500;
        double timeFadeTexts = 1500;
        double timeShrinkFlames = 3000;
        double timeFadeFlames = 1500;
        double timeFadeBackground = 2000;
        double timeWaitInTheDark = 200;
        double timeFadeInLogin = 500;

        try {
            URL loginUrl = getClass().getResource("/it/polimi/gc06/mesos/fxml/Login.fxml");
            if (loginUrl == null) {
                System.err.println("Error: Login.fxml hasn't been found");
                return;
            }

            FXMLLoader loader = new FXMLLoader(loginUrl);
            Parent loginRoot = loader.load();

            if (rootPane.getScene() != null) {
                rootPane.getScene().setFill(Color.BLACK);
            }

            if (pressKeyLabel != null) {
                pressKeyLabel.setVisible(false);
            }
            if (glowPulse != null) {
                glowPulse.stop();
            }

            ParallelTransition shutdown = new ParallelTransition();

            if (flamesImageView != null) {
                flamesImageView.translateYProperty().unbind();

                TranslateTransition moveFlames = new TranslateTransition(Duration.millis(timeShrinkFlames), flamesImageView);
                moveFlames.setByY(1000);

                FadeTransition fadeFlames = new FadeTransition(Duration.millis(timeFadeFlames), flamesImageView);
                fadeFlames.setToValue(0.5);

                shutdown.getChildren().addAll(moveFlames, fadeFlames);
            }

            if (particlesImageView != null) {
                FadeTransition fadeParticles = new FadeTransition(Duration.millis(timeFadeParticles), particlesImageView);
                fadeParticles.setToValue(0.0);
                shutdown.getChildren().add(fadeParticles);
            }

            if (titleLabel != null) {
                FadeTransition fadeTitle = new FadeTransition(Duration.millis(timeFadeTexts), titleLabel);
                fadeTitle.setToValue(0.0);
                shutdown.getChildren().add(fadeTitle);
            }

            if (bottomGlow != null) {
                FadeTransition fadeGlow = new FadeTransition(Duration.millis(timeFadeParticles), bottomGlow);
                fadeGlow.setToValue(0.0);
                shutdown.getChildren().add(fadeGlow);
            }

            if (backgroundImageView != null) {
                FadeTransition fadeBackground = new FadeTransition(Duration.millis(timeFadeBackground), backgroundImageView);
                fadeBackground.setToValue(0.0);
                shutdown.getChildren().add(fadeBackground);
            }

            shutdown.setOnFinished(e -> {
                GUI.primaryStage.setScene(new Scene(loginRoot, GUI.WIDTH, GUI.HEIGHT));
                loginRoot.setOpacity(0.0);

                PauseTransition pauseTransition = new PauseTransition(Duration.millis(timeWaitInTheDark));

                pauseTransition.setOnFinished(event2 -> {
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(timeFadeInLogin), loginRoot);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                });

                pauseTransition.play();
            });

            shutdown.play();

        } catch (IOException e) {
            System.err.println("Error I/O in loading Login.fxml");
            e.printStackTrace();
        }
    }
}