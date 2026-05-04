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
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.util.Duration;


public class OffertTileView extends TileView {
    private ImageView totemOverlay;
    private Totem totem;
    private String playerName;
    private Popup playerNamePopup;

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
    }

    public void setupPlayerNamePopup() {
        this.playerNamePopup = createPopup();

        totemOverlay.setOnMouseEntered((event) -> {
            playerNamePopup.show(totemOverlay, event.getScreenX(), event.getScreenY());
            playPopupIn(playerNamePopup);
        });

        totemOverlay.setOnMouseMoved((event) -> {
            playerNamePopup.setX(event.getScreenX() - 35);
            playerNamePopup.setY(event.getScreenY() - 50);
        });

        totemOverlay.setOnMouseExited((event) -> {
            playPopupOut(playerNamePopup);
        });
    }

    public void setTotem(Totem totem) {
        this.totem = totem;
        setupTotemOverlay();
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        setupPlayerNamePopup();

    }

    public Popup createPopup() {
        if (totem == Totem.NONE) {
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
        playerNameText.setFont(Font.font("System", FontWeight.BOLD, 13));
        playerNameText.setMouseTransparent(true);

        popupContent.setOpacity(0);

        popupContent.getChildren().add(playerNameText);
        popup.getContent().add(popupContent);

        return popup;
    }

    private void playPopupIn(Popup popup) {
        if (popup.getContent().isEmpty()) return;
        HBox content = (HBox) popup.getContent().get(0);
        content.setOpacity(0);
        content.setTranslateY(6);

        FadeTransition fade = new FadeTransition(Duration.millis(100), content);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition slide = new TranslateTransition(Duration.millis(100), content);
        slide.setFromY(6);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);

        new ParallelTransition(fade, slide).play();
    }

    private void playPopupOut(Popup popup) {
        if (popup.getContent().isEmpty()) return;
        if (!popup.isShowing()) return;
        HBox content = (HBox) popup.getContent().get(0);

        FadeTransition fade = new FadeTransition(Duration.millis(100), content);
        fade.setFromValue(content.getOpacity());
        fade.setToValue(0);
        fade.setInterpolator(Interpolator.EASE_IN);
        fade.setOnFinished(e -> popup.hide());

        fade.play();
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
