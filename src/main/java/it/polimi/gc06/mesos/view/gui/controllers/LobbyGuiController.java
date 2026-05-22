package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.view.gui.GUI;
import it.polimi.gc06.mesos.view.gui.GameScene;
import it.polimi.gc06.mesos.view.gui.ImageFetcher;
import it.polimi.gc06.mesos.view.gui.elements.LobbyPlayerView;
import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static it.polimi.gc06.mesos.view.gui.GUI.guidtovisitor;

public class LobbyGuiController {

    @FXML
    public StackPane lobbyRoot;
    @FXML
    public ImageView backgroundImage;
    @FXML
    public VBox mainContent;
    @FXML
    public HBox lobbyContainer;
    @FXML
    public HBox totemSelectionBox;

    @FXML
    public void initialize() {
        setupArchitecturalLayout();

        guidtovisitor.setLobbyGuiController(this);

        refreshLobbyUI();
    }

    private void setupArchitecturalLayout() {
        backgroundImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/imgs/background/mesos.png"))));
        backgroundImage.fitWidthProperty().bind(lobbyRoot.widthProperty());
        backgroundImage.fitHeightProperty().bind(lobbyRoot.heightProperty());

        mainContent.prefWidthProperty().bind(lobbyRoot.widthProperty());
        mainContent.prefHeightProperty().bind(lobbyRoot.heightProperty());

        lobbyContainer.prefHeightProperty().bind(mainContent.heightProperty().multiply(0.6));
        totemSelectionBox.prefHeightProperty().bind(mainContent.heightProperty().multiply(0.3));

        VBox.setVgrow(lobbyContainer, Priority.ALWAYS);
    }

    public void refreshLobbyUI() {
        Platform.runLater(() -> {
            drawPlayers();
            drawTotemSelectionBox();
        });
    }

    public void drawPlayers() {
        lobbyContainer.getChildren().clear();

        if (GUI.smallModel.getPlayer().getColor() != null) {
            Totem myTotem = mapColorToTotem(GUI.smallModel.getPlayer().getColor());
            LobbyPlayerView playerBox = initPlayerBox(GUI.smallModel.getPlayer().getNickname(), "Ready", myTotem);
            addPlayerWithAnimation(playerBox);
        }

        for (var opponent : GUI.smallModel.getOpponents()) {
            if (opponent.getColor() != null) {
                Totem oppTotem = mapColorToTotem(opponent.getColor());
                LobbyPlayerView oppBox = initPlayerBox(opponent.getNickname(), "Ready", oppTotem);
                addPlayerWithAnimation(oppBox);
            }
        }
    }

    private LobbyPlayerView initPlayerBox(String playerNameLabel, String status, Totem totem) {
        LobbyPlayerView playerBox = new LobbyPlayerView(playerNameLabel, status, totem);
        playerBox.prefWidthProperty().bind(lobbyRoot.widthProperty().divide(5.5));
        playerBox.prefHeightProperty().bind(lobbyContainer.heightProperty());
        return playerBox;
    }

    private void addPlayerWithAnimation(LobbyPlayerView playerBox) {
        playerBox.setOpacity(0);
        playerBox.setTranslateY(10);
        playerBox.setScaleX(0.95);
        playerBox.setScaleY(0.95);

        lobbyContainer.getChildren().add(playerBox);

        FadeTransition fade = new FadeTransition(Duration.millis(350), playerBox);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(350), playerBox);
        slide.setToY(0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(350), playerBox);
        scale.setToX(1.0);
        scale.setToY(1.0);

        new ParallelTransition(fade, slide, scale).play();
    }


    public void drawTotemSelectionBox() {
        totemSelectionBox.getChildren().clear();

        if (GUI.smallModel == null || GUI.smallModel.getOpponents() == null || GUI.smallModel.getOpponents().isEmpty()) {
            Label waitingLabel = new Label("In attesa degli altri giocatori...");
            waitingLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");
            totemSelectionBox.getChildren().add(waitingLabel);
            return;
        }

        List<Totem> occupiedTotems = new ArrayList<>();

        if (GUI.smallModel.getPlayer().getColor() != null) {
            occupiedTotems.add(mapColorToTotem(GUI.smallModel.getPlayer().getColor()));
        }
        for (var opponent : GUI.smallModel.getOpponents()) {
            if (opponent.getColor() != null) {
                occupiedTotems.add(mapColorToTotem(opponent.getColor()));
            }
        }

        if (GUI.smallModel.getPlayer().getColor() != null) {
            return;
        }

        int availableCount = 0;
        for (Totem t : Totem.values()) {
            if (t != Totem.NONE && !occupiedTotems.contains(t)) availableCount++;
        }

        if (availableCount == 0) return;

        for (Totem totem : Totem.values()) {
            if (totem == Totem.NONE || occupiedTotems.contains(totem)) {
                continue;
            }

            HBox totemBox = new HBox();
            totemBox.setAlignment(Pos.CENTER);
            totemBox.prefWidthProperty().bind(totemSelectionBox.widthProperty().divide(availableCount).multiply(0.6));
            totemBox.prefHeightProperty().bind(totemSelectionBox.heightProperty());
            totemBox.setStyle("-fx-background-color: rgb(" + totem.getTotemColorRGBbrighter() + "); -fx-border-color: rgb( " + totem.getTotemColorRGB() + "); -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
            totemBox.setPadding(new Insets(20));
            totemBox.setCursor(Cursor.HAND);

            ImageView totemImage = new ImageView(new Image(getClass().getResourceAsStream(totem.getTotemStanding())));
            totemImage.fitHeightProperty().bind(totemBox.heightProperty().multiply(0.8));
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

            setupTotemHoverAnimations(totemBox);

            totemBox.setOnMouseClicked(e -> {
                totemSelectionBox.setDisable(true);
                try {
                    System.out.println("Sending server the totem choice: " + totem);
                    GUI.client.getServerConnection().chooseTotemColor(LoginController.getNickname(), mapTotemToColor(totem));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    totemSelectionBox.setDisable(false);
                }
            });
        }
    }

    private void setupTotemHoverAnimations(HBox totemBox) {
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
    }

    private it.polimi.gc06.mesos.model.Color mapTotemToColor(Totem totem) {
        return switch (totem.name().toUpperCase()) {
            case "ORANGE" -> it.polimi.gc06.mesos.model.Color.ORANGE;
            case "WHITE" -> it.polimi.gc06.mesos.model.Color.WHITE;
            case "TURQUOISE" -> it.polimi.gc06.mesos.model.Color.TURQUOISE;
            case "YELLOW" -> it.polimi.gc06.mesos.model.Color.YELLOW;
            default -> it.polimi.gc06.mesos.model.Color.PURPLE;
        };
    }

    private Totem mapColorToTotem(it.polimi.gc06.mesos.model.Color color) {
        if (color == null) return Totem.NONE;
        return switch (color) {
            case WHITE -> Totem.WHITE;
            case ORANGE -> Totem.ORANGE;
            case TURQUOISE -> Totem.TURQUOISE;
            case YELLOW -> Totem.YELLOW;
            case PURPLE -> Totem.PURPLE;
        };
    }

    public void checkAndStartGame() {
        if (GUI.smallModel.getPlayer().getColor() == null) {
            return;
        }

        for (var opponent : GUI.smallModel.getOpponents()) {
            if (opponent.getColor() == null) {
                return;
            }
        }

        System.out.println("All players are in. Game is starting...");

        try {
            if (GUI.imageFetcher == null) {
                GUI.imageFetcher = new ImageFetcher(GUI.smallModel.getOpponents().size() + 1);
            }

            Region blackOverlay = new Region();
            blackOverlay.setStyle("-fx-background-color: black;");
            blackOverlay.setOpacity(0.0);
            blackOverlay.setMouseTransparent(true);

            blackOverlay.prefWidthProperty().bind(lobbyRoot.widthProperty());
            blackOverlay.prefHeightProperty().bind(lobbyRoot.heightProperty());

            lobbyRoot.getChildren().add(blackOverlay);

            FadeTransition fadeToBlack = new FadeTransition(Duration.seconds(1.5), blackOverlay);
            fadeToBlack.setFromValue(0.0);
            fadeToBlack.setToValue(1.0);

            fadeToBlack.setOnFinished(e -> {
                GUI.changeScene(GameScene.GAME.getPath());
            });

            fadeToBlack.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
