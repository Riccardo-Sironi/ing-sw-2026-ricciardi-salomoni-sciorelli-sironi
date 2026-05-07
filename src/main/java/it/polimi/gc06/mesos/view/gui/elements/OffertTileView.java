package it.polimi.gc06.mesos.view.gui.elements;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;

public class OffertTileView extends TileView {

    private Point2D centerSlot = new Point2D(0.50, 0.50);

    private Totem totem;
    private String playerName;
    private Popup playerNamePopup;
    private TotemPieceView currentTotemView;

    private final Font mesosFont = Font.loadFont(this.getClass().getResourceAsStream("/it/polimi/gc06/mesos/fonts/KidKnowledge.otf"), 20);

    public OffertTileView(Image image) {
        super(image);
        this.totem = Totem.NONE;
        this.imageView.setPreserveRatio(true);
    }

    private DoubleBinding getTrueWidthBinding() {
        return Bindings.createDoubleBinding(() -> {
            if (imageView.getImage() == null || imageView.getFitWidth() <= 0 || imageView.getFitHeight() <= 0) return 0.0;
            double scale = Math.min(imageView.getFitWidth() / imageView.getImage().getWidth(), imageView.getFitHeight() / imageView.getImage().getHeight());
            return imageView.getImage().getWidth() * scale;
        }, imageView.fitWidthProperty(), imageView.fitHeightProperty(), imageView.imageProperty());
    }

    private DoubleBinding getTrueHeightBinding() {
        return Bindings.createDoubleBinding(() -> {
            if (imageView.getImage() == null || imageView.getFitWidth() <= 0 || imageView.getFitHeight() <= 0) return 0.0;
            double scale = Math.min(imageView.getFitWidth() / imageView.getImage().getWidth(), imageView.getFitHeight() / imageView.getImage().getHeight());
            return imageView.getImage().getHeight() * scale;
        }, imageView.fitWidthProperty(), imageView.fitHeightProperty(), imageView.imageProperty());
    }

    public void setCenterPercentage(double xPercent, double yPercent) {
        this.centerSlot = new Point2D(xPercent, yPercent);

        if (this.currentTotemView != null) {

            DoubleBinding trueW = getTrueWidthBinding();
            DoubleBinding trueH = getTrueHeightBinding();

            double imageRatio = this.currentTotemView.getImage().getWidth() / this.currentTotemView.getImage().getHeight();
            DoubleBinding totemInstantWidth = this.currentTotemView.fitHeightProperty().multiply(imageRatio);

            this.currentTotemView.layoutXProperty().bind(trueW.multiply(centerSlot.getX()).subtract(totemInstantWidth.divide(2)));
            this.currentTotemView.layoutYProperty().bind(trueH.multiply(centerSlot.getY()).subtract(this.currentTotemView.fitHeightProperty().divide(2)));
        }
    }

    public void setTotem(Totem totemType) {
        this.totem = totemType;

        this.getChildren().removeIf(node -> node instanceof Pane);
        if (totemType == Totem.NONE) return;

        Pane layer = new Pane();
        DoubleBinding trueW = getTrueWidthBinding();
        DoubleBinding trueH = getTrueHeightBinding();

        layer.maxWidthProperty().bind(trueW);
        layer.maxHeightProperty().bind(trueH);
        layer.prefWidthProperty().bind(trueW);
        layer.prefHeightProperty().bind(trueH);

        this.currentTotemView = new TotemPieceView(totemType);

        this.currentTotemView.fitHeightProperty().bind(trueH.multiply(0.25));

        double imageRatio = this.currentTotemView.getImage().getWidth() / this.currentTotemView.getImage().getHeight();
        DoubleBinding totemInstantWidth = this.currentTotemView.fitHeightProperty().multiply(imageRatio);

        this.currentTotemView.layoutXProperty().bind(trueW.multiply(centerSlot.getX()).subtract(totemInstantWidth.divide(2)));
        this.currentTotemView.layoutYProperty().bind(trueH.multiply(centerSlot.getY()).subtract(this.currentTotemView.fitHeightProperty().divide(2)));

        layer.getChildren().add(this.currentTotemView);
        this.getChildren().add(layer);

        if (this.playerName != null) {
            setupPlayerNamePopup();
        }
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        if (this.playerNamePopup != null) {
            this.playerNamePopup.hide();
        }
        setupPlayerNamePopup();
    }

    private void setupPlayerNamePopup() {
        if (this.currentTotemView == null) return;

        this.playerNamePopup = createPopup();

        this.currentTotemView.setOnMouseEntered((event) -> {
            playerNamePopup.show(this.currentTotemView, event.getScreenX(), event.getScreenY());
            EffectsManager.playPopupIn(playerNamePopup);
        });

        this.currentTotemView.setOnMouseMoved((event) -> {
            playerNamePopup.setX(event.getScreenX() - 35);
            playerNamePopup.setY(event.getScreenY() - 60);
        });

        this.currentTotemView.setOnMouseExited((event) -> {
            EffectsManager.playPopupOut(playerNamePopup);
        });
    }

    private Popup createPopup() {
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
        if (mesosFont != null) {
            playerNameText.setFont(mesosFont);
        }
        playerNameText.setMouseTransparent(true);

        popupContent.setOpacity(0);
        popupContent.getChildren().add(playerNameText);
        popup.getContent().add(popupContent);

        return popup;
    }

    public Totem getTotem() {
        return this.totem;
    }

    public String getPlayerName() {
        return this.playerName;
    }
}