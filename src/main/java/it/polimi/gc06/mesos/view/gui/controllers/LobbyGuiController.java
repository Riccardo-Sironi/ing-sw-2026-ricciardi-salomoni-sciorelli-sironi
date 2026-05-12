package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;


public class LobbyGuiController {
    @FXML
    public StackPane lobbyRoot;
    @FXML
    public HBox lobbyContainer;

    public ArrayList<VBox> players;

    private final Font mesosFont = Font.loadFont(
            this.getClass().getResourceAsStream(
                    "/it/polimi/gc06/mesos/fonts/KidKnowledge.otf"
            ), 25
    );

    @FXML
    public void initialize() {
        players = new ArrayList<>();
        setupArchitecturalLayout();

        Timeline demoTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e ->
                        addPlayer(initPlayerBox("Player 1", "Ready", Totem.TURQUOISE))),
                new KeyFrame(Duration.seconds(2), e ->
                        addPlayer(initPlayerBox("Player 2", "Ready", Totem.PURPLE))),
                new KeyFrame(Duration.seconds(3), e ->
                        addPlayer(initPlayerBox("Player 3", "Ready", Totem.WHITE))),
                new KeyFrame(Duration.seconds(4), e ->
                        addPlayer(initPlayerBox("Player 4", "Ready", Totem.YELLOW))),
                new KeyFrame(Duration.seconds(5), e ->
                        addPlayer(initPlayerBox("Player 5", "Ready", Totem.ORANGE)))
        );

        demoTimeline.play();
    }

    private void setupArchitecturalLayout() {
        lobbyContainer.prefWidthProperty().bind(lobbyRoot.widthProperty());
        lobbyContainer.prefHeightProperty().bind(lobbyRoot.heightProperty());
        lobbyContainer.setAlignment(Pos.CENTER);
        lobbyContainer.setSpacing(20);
        lobbyContainer.setFillHeight(false);
        HBox.setHgrow(lobbyContainer, Priority.NEVER);
    }

    private VBox initPlayerBox(String playerNameLabel, String status, Totem totem) {
        VBox playerBox = new VBox();
        playerBox.prefWidthProperty().bind(lobbyRoot.widthProperty().divide(5));
        playerBox.prefHeightProperty().bind(lobbyRoot.heightProperty().multiply(0.6));
        playerBox.setAlignment(Pos.CENTER);
        playerBox.setSpacing(40);
        playerBox.setStyle("-fx-background-color: rgb(" + totem.getTotemColorRGB() + ",0.6); -fx-border-color: rgb( " + totem.getTotemColorRGB() + "); -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        ImageView totemImage = new ImageView(totem.getTotemStanding());
        totemImage.setPreserveRatio(true);
        totemImage.fitHeightProperty().bind(playerBox.heightProperty().multiply(0.3));
        DropShadow shadow = new DropShadow();
        shadow.setRadius(2.0);
        shadow.setOffsetX(-3.0);
        shadow.setOffsetY(3);
        shadow.setColor(Color.color(0, 0, 0, 0.5));
        totemImage.setEffect(shadow);
        totemImage.setCursor(Cursor.HAND);

        Text playerName = new Text(playerNameLabel);
        playerName.setTextAlignment(TextAlignment.CENTER);
        playerName.setFont(mesosFont);
        playerName.wrappingWidthProperty().bind(playerBox.widthProperty().subtract(20));
        playerName.setFill(totem.getTotemColor());

        Text playerStatus = new Text(status.toUpperCase());
        playerStatus.setTextAlignment(TextAlignment.CENTER);
        playerStatus.setFont(mesosFont);
        playerStatus.setScaleX(1.5);
        playerStatus.setScaleY(1.5);
        playerStatus.wrappingWidthProperty().bind(playerBox.widthProperty().subtract(20));
        playerStatus.setFill(totem.getTotemColor());

        playerBox.getChildren().addAll(totemImage, playerName, playerStatus);

        // TODO : the effect below is what we'll use in the color selection process

//        ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.2), playerBox);
//        ScaleTransition scaleOut = new ScaleTransition(Duration.seconds(0.2), playerBox);
//
//        playerBox.setOnMouseEntered(e -> {
//            scaleOut.stop();
//            scaleIn.setToX(1.1);
//            scaleIn.setToY(1.1);
//            scaleIn.playFromStart();
//        });
//
//        playerBox.setOnMouseExited(e -> {
//            scaleIn.stop();
//            scaleOut.setToX(1.0);
//            scaleOut.setToY(1.0);
//            scaleOut.playFromStart();
//        });

        return playerBox;
    }

    public void addPlayer(VBox playerBox) {
        players.add(playerBox);

        // effect when a new player joins: fade in + slide up + scale up
        playerBox.setOpacity(0);
        playerBox.setTranslateY(10);
        playerBox.setScaleX(0.95);
        playerBox.setScaleY(0.95);

        lobbyContainer.getChildren().add(playerBox);

        FadeTransition fade = new FadeTransition(Duration.millis(350), playerBox);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(350), playerBox);
        slide.setFromY(10);
        slide.setToY(0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(350), playerBox);
        scale.setFromX(0.95);
        scale.setFromY(0.95);
        scale.setToX(1.0);
        scale.setToY(1.0);

        ParallelTransition enterAnimation = new ParallelTransition(fade, slide, scale);
        enterAnimation.play();
    }
}
