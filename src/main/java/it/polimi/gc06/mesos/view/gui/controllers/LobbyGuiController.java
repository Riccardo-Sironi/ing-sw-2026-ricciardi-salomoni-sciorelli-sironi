package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.view.gui.elements.LobbyPlayerView;
import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.EnumMap;


public class LobbyGuiController {
    @FXML
    public StackPane lobbyRoot;
    @FXML
    public HBox lobbyContainer;
    @FXML
    public ImageView backgroundImage;
    @FXML
    public HBox totemSelectionBox;

    // TODO : this should be based on the small model
    public EnumMap<Totem, LobbyPlayerView> players;


    @FXML
    public void initialize() {
        players = new EnumMap<>(Totem.class);

        setupArchitecturalLayout();

        drawTotemSelectionBox();
    }

    private void setupArchitecturalLayout() {
        lobbyContainer.prefWidthProperty().bind(lobbyRoot.widthProperty());
        lobbyContainer.prefHeightProperty().bind(lobbyRoot.heightProperty());
        lobbyContainer.setAlignment(Pos.CENTER);
        lobbyContainer.setSpacing(20);
        lobbyContainer.setFillHeight(false);
        HBox.setHgrow(lobbyContainer, Priority.NEVER);

        backgroundImage.setImage(new Image("mesos.png"));
        backgroundImage.fitHeightProperty().bind(lobbyRoot.heightProperty());
        backgroundImage.fitWidthProperty().bind(lobbyRoot.widthProperty());

        totemSelectionBox.prefWidthProperty().bind(lobbyRoot.widthProperty().multiply(0.6));
        totemSelectionBox.prefHeightProperty().bind(lobbyRoot.heightProperty().multiply(0.4));
        totemSelectionBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        totemSelectionBox.setSpacing(20);
        totemSelectionBox.setAlignment(Pos.CENTER);
        totemSelectionBox.setFillHeight(false);
        HBox.setHgrow(totemSelectionBox, Priority.NEVER);

        StackPane.setAlignment(totemSelectionBox, Pos.CENTER);
    }

    private LobbyPlayerView initPlayerBox(String playerNameLabel, String status, Totem totem) {
        LobbyPlayerView playerBox = new LobbyPlayerView(playerNameLabel, status, totem);
        playerBox.prefWidthProperty().bind(lobbyRoot.widthProperty().divide(5));
        playerBox.prefHeightProperty().bind(lobbyRoot.heightProperty().multiply(0.6));

        return playerBox;
    }

    public void drawPlayers() {
        for (LobbyPlayerView lobbyPlayerView : players.values()) {
            if (!lobbyContainer.getChildren().contains(lobbyPlayerView)) {
                addPlayer(lobbyPlayerView, lobbyPlayerView.getTotem());
            }
        }
    }

    public void drawTotemSelectionBox() {
        totemSelectionBox.getChildren().clear();

        for (Totem totem : Totem.values()) {
            if (!players.containsKey(totem) && totem != Totem.NONE) {
                HBox totemBox = new HBox();
                totemBox.setAlignment(Pos.CENTER);
                totemBox.prefWidthProperty().bind(
                        totemSelectionBox.widthProperty().divide(5 - players.size()).multiply(0.2)
                );
                totemBox.prefHeightProperty().bind(
                        totemSelectionBox.heightProperty().multiply(0.2)
                );
                totemBox.setStyle("-fx-background-color: rgb(" + totem.getTotemColorRGBbrighter() + "); -fx-border-color: rgb( " + totem.getTotemColorRGB() + "); -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
                totemBox.setPadding(new Insets(60));
                totemBox.setCursor(Cursor.HAND);

                ImageView totemImage = new ImageView(totem.getTotemStanding());
                totemImage.fitHeightProperty().bind(totemSelectionBox.heightProperty().multiply(0.4));
                totemImage.setPreserveRatio(true);

                DropShadow shadow = new DropShadow();
                shadow.setRadius(2.0);
                shadow.setOffsetX(-1.0);
                shadow.setSpread(0.2);
                shadow.setOffsetY(3);
                shadow.setColor(Color.color(0, 0, 0, 0.5));
                totemImage.setEffect(shadow);

                totemBox.getChildren().add(totemImage);
                totemSelectionBox.getChildren().add(totemBox);

                ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.15), totemBox);
                ScaleTransition scaleOut = new ScaleTransition(Duration.seconds(0.15), totemBox);
                TranslateTransition translateIn = new TranslateTransition(Duration.seconds(0.15), totemBox);
                TranslateTransition translateOut = new TranslateTransition(Duration.seconds(0.15), totemBox);

                totemBox.setOnMouseEntered(e -> {
                    scaleOut.stop();
                    translateOut.stop();
                    translateIn.setToY(-20);
                    scaleIn.setToX(1.05);
                    scaleIn.setToY(1.05);
                    translateIn.playFromStart();
                    scaleIn.playFromStart();
                });

                totemBox.setOnMouseExited(e -> {
                    translateIn.stop();
                    scaleIn.stop();
                    translateOut.setToY(0);
                    scaleOut.setToX(1.0);
                    scaleOut.setToY(1.0);
                    translateOut.playFromStart();
                    scaleOut.playFromStart();
                });

                totemBox.setOnMouseClicked(e -> {
                            players.put(totem, initPlayerBox("Player " + (players.size() + 1), "Ready", totem));
                            drawPlayers();
                            drawTotemSelectionBox();
                        }
                );
            }
        }
    }

    public void addPlayer(LobbyPlayerView playerBox, Totem totem) {
        players.put(totem, playerBox);
        
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
