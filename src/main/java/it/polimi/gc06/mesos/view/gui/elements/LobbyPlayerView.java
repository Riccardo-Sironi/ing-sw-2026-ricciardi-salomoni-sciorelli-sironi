package it.polimi.gc06.mesos.view.gui.elements;

import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class LobbyPlayerView extends VBox {
    private String playerNameLabel;
    private String status;
    private Totem totem;

    private final ImageView totemImage;
    private final Text playerNameText;
    private final Text statusText;

    private final Font mesosFont = Font.loadFont(
            this.getClass().getResourceAsStream(
                    "/it/polimi/gc06/mesos/fonts/KidKnowledge.otf"
            ), 25
    );

    public LobbyPlayerView(String playerNameLabel, String status, Totem totem) {
        super();
        this.playerNameLabel = playerNameLabel;
        this.status = status;
        this.totem = totem;

        this.setAlignment(Pos.CENTER);
        this.setSpacing(40);
        this.setStyle("-fx-background-color: rgb(" + totem.getTotemColorRGBbrighter() + "); -fx-border-color: rgb( " + totem.getTotemColorRGB() + "); -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        totemImage = new ImageView(totem.getTotemStanding());
        totemImage.setPreserveRatio(true);
        totemImage.fitHeightProperty().bind(this.heightProperty().multiply(0.3));
        DropShadow shadow = new DropShadow();
        shadow.setRadius(2.0);
        shadow.setOffsetX(-1.0);
        shadow.setSpread(0.2);
        shadow.setOffsetY(3);
        shadow.setColor(Color.color(0, 0, 0, 0.5));
        totemImage.setEffect(shadow);
        totemImage.setCursor(Cursor.HAND);

        playerNameText = new Text(playerNameLabel);
        playerNameText.setTextAlignment(TextAlignment.CENTER);
        playerNameText.setFont(mesosFont);
        playerNameText.setScaleX(1.2);
        playerNameText.setScaleY(1.2);
        playerNameText.wrappingWidthProperty().bind(this.widthProperty().subtract(20));
        playerNameText.setFill(totem.getTotemColor());

        statusText = new Text(status.toUpperCase());
        statusText.setTextAlignment(TextAlignment.CENTER);
        statusText.setFont(mesosFont);
        statusText.setScaleX(1.7);
        statusText.setScaleY(1.7);
        statusText.wrappingWidthProperty().bind(this.widthProperty().subtract(20));
        statusText.setFill(totem.getTotemColor());

        this.getChildren().addAll(totemImage, playerNameText, statusText);
    }

    public Totem getTotem() {
        return totem;
    }

    public void setTotem(Totem totem) {
        this.totem = totem;
        totemImage.setImage(new Image("totem.getTotemStanding()"));
    }

    public String getPlayerNameLabel() {
        return playerNameLabel;
    }

    public void setPlayerNameLabel(String playerNameLabel) {
        this.playerNameLabel = playerNameLabel;
        playerNameText.setText(playerNameLabel);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        statusText.setText(status);
    }
}
