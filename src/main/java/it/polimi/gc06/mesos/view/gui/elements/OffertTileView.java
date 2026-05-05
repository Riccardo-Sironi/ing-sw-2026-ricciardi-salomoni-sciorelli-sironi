package it.polimi.gc06.mesos.view.gui.elements;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.util.Duration;


public class OffertTileView extends TileView {
    private ImageView totemOverlay;
    private Totem totem;
    private String playerName;
    private Popup playerNamePopup;

    private final Font mesosFont = Font.loadFont(this.getClass().getResourceAsStream("/it/polimi/gc06/mesos/fonts/KidKnowledge.otf"), 20);

    public OffertTileView(Image image) {
        super(image);
        this.totem = Totem.NONE;
        setupTotemOverlay();
        this.getChildren().add(totemOverlay);
    }

    public OffertTileView(Image image, Totem totem) {
        super(image);

        this.totem = totem;

        setupTotemOverlay();

        this.getChildren().add(totemOverlay);
    }

    public OffertTileView(Image image, Totem totem, String playerName) {
        super(image);

        this.playerName = playerName;
        this.totem = totem;

        setupTotemOverlay();
        setupPlayerNamePopup();

        this.getChildren().add(totemOverlay);
    }

    public void setupTotemOverlay() {
        totemOverlay = new ImageView(new Image(this.totem.getTotemOverlay()));
        totemOverlay.setPreserveRatio(true);
        totemOverlay.setSmooth(true);
        totemOverlay.setStyle("-fx-cursor: hand");
        totemOverlay.setFitWidth(USE_COMPUTED_SIZE);
    }

    public void setupPlayerNamePopup() {
        this.playerNamePopup = createPopup();

        totemOverlay.setOnMouseEntered((event) -> {
            playerNamePopup.show(totemOverlay, event.getScreenX(), event.getScreenY());
            PopupEffectsManager.playPopupIn(playerNamePopup);
        });

        totemOverlay.setOnMouseMoved((event) -> {
            playerNamePopup.setX(event.getScreenX() - 35);
            playerNamePopup.setY(event.getScreenY() - 60);
        });

        totemOverlay.setOnMouseExited((event) -> {
            PopupEffectsManager.playPopupOut(playerNamePopup);
        });
    }

    public void setTotem(Totem totem) {
        this.totem = totem;
        totemOverlay.setImage(new Image(this.totem.getTotemOverlay()));
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        if (this.playerNamePopup != null) {
            this.playerNamePopup.hide(); // chiudi il vecchio prima di sostituirlo
        }
        setupPlayerNamePopup();
    }

    public Popup createPopup() {
        if (totem == Totem.NONE || playerName == null) {
            return new Popup();
        }

        Popup popup = new Popup();
        popup.setAutoFix(true);

        HBox popupContent = new HBox();
        popupContent.setStyle(
                "-fx-background-color: " + totem.getTotemColorHex() + ";" +
                        "-fx-background-radius: 8px;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.6);" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-padding: 8px 15px;"
        );
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setMouseTransparent(true);

        Text playerNameText = new Text(playerName);
        playerNameText.setFill(Color.WHITE);
        playerNameText.setFont(mesosFont);
        playerNameText.setMouseTransparent(true);

        popupContent.setOpacity(0);

        popupContent.getChildren().add(playerNameText);
        popup.getContent().add(popupContent);

        return popup;
    }

    public ImageView getOverlay() {
        return this.totemOverlay;
    }

    public Popup getPlayerNamePopup() {
        return this.playerNamePopup;
    }

    public Totem getTotem() {
        return this.totem;
    }

    public String getPlayerName() {
        return this.playerName;
    }
}
