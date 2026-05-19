package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.view.gui.GUI;
import it.polimi.gc06.mesos.view.gui.elements.LobbyPlayerView;
import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.gc06.mesos.view.gui.GUI.guidtovisitor;
import static it.polimi.gc06.mesos.view.gui.GUI.smallModel;

public class LobbyGuiController implements ModelListener {

    @FXML public StackPane lobbyRoot;
    @FXML public ImageView backgroundImage;
    @FXML public VBox mainContent;
    @FXML public HBox lobbyContainer;
    @FXML public HBox totemSelectionBox;

    @FXML
    public void initialize() {
        setupArchitecturalLayout();

        guidtovisitor.setLobbyGuiController(this);

        refreshLobbyUI();
    }

    private void setupArchitecturalLayout() {
        backgroundImage.setImage(new Image(getClass().getResourceAsStream("/imgs/background/mesos.png")));
        backgroundImage.fitWidthProperty().bind(lobbyRoot.widthProperty());
        backgroundImage.fitHeightProperty().bind(lobbyRoot.heightProperty());

        mainContent.prefWidthProperty().bind(lobbyRoot.widthProperty());
        mainContent.prefHeightProperty().bind(lobbyRoot.heightProperty());

        lobbyContainer.prefHeightProperty().bind(mainContent.heightProperty().multiply(0.6));
        totemSelectionBox.prefHeightProperty().bind(mainContent.heightProperty().multiply(0.3));

        VBox.setVgrow(lobbyContainer, Priority.ALWAYS);
    }

    public void refreshLobbyUI() {
        drawPlayers();
        drawTotemSelectionBox();
    }

    public void drawPlayers() {
        lobbyContainer.getChildren().clear();

        // TODO: Adatta questa riga in base a come il tuo SmallModel memorizza i giocatori connessi.
        // Esempio ipotetico: smallModel.getPlayers() restituisce una lista di oggetti PlayerView (o simili)
        // che contengono il nickname, lo stato (Ready/Not Ready) e il Totem scelto.

        /* for (PlayerView p : smallModel.getPlayers()) {
            // Se il giocatore ha già scelto un totem (quindi != Totem.NONE o null)
            if (p.getTotem() != null && p.getTotem() != Totem.NONE) {
                LobbyPlayerView playerBox = initPlayerBox(p.getNickname(), "Ready", p.getTotem());
                addPlayerWithAnimation(playerBox);
            }
        }
        */
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

        List<Totem> occupiedTotems = new ArrayList<>();
        /*
        for (PlayerView p : smallModel.getPlayers()) {
            if (p.getTotem() != null) occupiedTotems.add(p.getTotem());
        }
        */

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

            totemBox.prefWidthProperty().bind(
                    totemSelectionBox.widthProperty().divide(availableCount).multiply(0.6)
            );
            totemBox.prefHeightProperty().bind(totemSelectionBox.heightProperty());
            totemBox.setStyle("-fx-background-color: rgb(" + totem.getTotemColorRGBbrighter() + "); -fx-border-color: rgb( " + totem.getTotemColorRGB() + "); -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
            totemBox.setPadding(new Insets(20));
            totemBox.setCursor(Cursor.HAND);

            ImageView totemImage = new ImageView(new Image(getClass().getResourceAsStream(totem.getTotemStanding())));
            totemImage.fitHeightProperty().bind(totemBox.heightProperty().multiply(0.8));
            totemImage.setPreserveRatio(true);

            DropShadow shadow = new DropShadow();
            shadow.setRadius(2.0); shadow.setOffsetX(-1.0); shadow.setSpread(0.2); shadow.setOffsetY(3);
            shadow.setColor(Color.color(0, 0, 0, 0.5));
            totemImage.setEffect(shadow);

            totemBox.getChildren().add(totemImage);
            totemSelectionBox.getChildren().add(totemBox);

            setupTotemHoverAnimations(totemBox);

            totemBox.setOnMouseClicked(e -> {
                totemSelectionBox.setDisable(true);

                try {
                    System.out.println("Inviando al server la scelta del totem: " + totem);
                    GUI.client.getServerConnection().chooseTotem(LoginController.getNickname(), totem.ordinal());
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
            scaleOut.stop(); translateOut.stop();
            translateIn.setToY(-20);
            scaleIn.setToX(1.05); scaleIn.setToY(1.05);
            translateIn.playFromStart(); scaleIn.playFromStart();
        });

        totemBox.setOnMouseExited(e -> {
            translateIn.stop(); scaleIn.stop();
            translateOut.setToY(0);
            scaleOut.setToX(1.0); scaleOut.setToY(1.0);
            translateOut.playFromStart(); scaleOut.playFromStart();
        });
    }

    @Override
    public void update(SmallModelEditor dto) {
        dto.accept(guidtovisitor);
    }
}
