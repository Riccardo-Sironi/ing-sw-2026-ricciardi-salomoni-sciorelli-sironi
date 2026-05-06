package it.polimi.gc06.mesos.view.gui.elements;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.scene.layout.HBox;

public class OffertTileView extends TileView {

    private ImageView totemOverlay; // version with transparent background and the totem piece

    private TotemPieceView totemPiece; // version with single images of the totem piece

    private Totem totem;
    private String playerName;

    private Popup playerNamePopup;

    private final Font mesosFont = Font.loadFont(
            this.getClass().getResourceAsStream("/it/polimi/gc06/mesos/fonts/KidKnowledge.otf"), 20
    );

    public OffertTileView(Image image) {
        super(image);
        this.totem = Totem.NONE;
        //setupTotemPiece();
        setupTotemOverlay();
    }

//    public OffertTileView(Image image, Totem totem) {
//        super(image);
//        this.totem = totem;
//        setupTotemOverlay();
//        //setupTotemPiece();
//    }
//
//    public OffertTileView(Image image, Totem totem, String playerName) {
//        super(image);
//        this.totem = totem;
//        this.playerName = playerName;
//        setupTotemOverlay();
//        //setupTotemPiece();
//        //setupPlayerNamePopup();
//    }

    private void setupTotemOverlay() {
        totemOverlay = new ImageView(new Image(this.totem.getTotemOverlay()));
        totemOverlay.setPreserveRatio(true);
        totemOverlay.setSmooth(true);
        totemOverlay.setStyle("-fx-cursor: hand");
        totemOverlay.setFitWidth(USE_COMPUTED_SIZE);

        this.getChildren().add(totemOverlay);
    }

    private void setupTotemPiece() {
        totemPiece = new TotemPieceView(totem);

        totemPiece.fitWidthProperty().unbind();
        totemPiece.fitWidthProperty().bind(
                this.widthProperty().multiply(0.32)
        );

        StackPane.setAlignment(totemPiece, Pos.CENTER);
        totemPiece.translateYProperty().bind(
                this.heightProperty().multiply(-0.2).add(-20)
        );

        this.getChildren().add(totemPiece);
    }

    public void setTotem(Totem totem) {
        this.totem = totem;
        totemOverlay.setImage(new Image(this.totem.getTotemOverlay()));
    }

//    public void setTotem(Totem totem) {
//        this.totem = totem;
//        this.totemPiece.setImage(new Image(this.totem.getTotemOverlay()));
//        this.getChildren().remove(totemPiece);
//        setupTotemPiece();
//        if (playerName != null) setupPlayerNamePopup();
//    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        if (this.playerNamePopup != null) {
            this.playerNamePopup.hide();
        }
        setupPlayerNamePopup(totemOverlay);
    }

//    public void setPlayerName(String playerName) {
//        this.playerName = playerName;
//        if (this.playerNamePopup != null) {
//            this.playerNamePopup.hide();
//        }
//        setupPlayerNamePopup(totemOverlay);
//    }

    private void setupPlayerNamePopup(ImageView actor) {
        if (actor == null) return;

        this.playerNamePopup = createPopup();

        actor.setOnMouseEntered((event) -> {
            playerNamePopup.show(actor, event.getScreenX(), event.getScreenY());
            EffectsManager.playPopupIn(playerNamePopup);
        });

        actor.setOnMouseMoved((event) -> {
            playerNamePopup.setX(event.getScreenX() - 35);
            playerNamePopup.setY(event.getScreenY() - 60);
        });

        actor.setOnMouseExited((event) -> {
            EffectsManager.playPopupOut(playerNamePopup);
        });
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

    public TotemPieceView getTotemPiece() {
        return this.totemPiece;
    }

    public ImageView getTotemOverlay() {
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