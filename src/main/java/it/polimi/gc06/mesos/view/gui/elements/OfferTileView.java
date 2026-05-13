package it.polimi.gc06.mesos.view.gui.elements;

import it.polimi.gc06.mesos.view.gui.helpers.EffectsManager;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;

public class OfferTileView extends TileView {

    private Point2D centerSlot = new Point2D(0.50, 0.50);
    private TotemPieceView currentTotemView;
    private Popup playerNamePopup;

    public OfferTileView(Image image) {
        super(image);
    }

    private DoubleBinding getTrueWidth() {
        return Bindings.createDoubleBinding(() -> {
            Image img = imageView.getImage();
            if (img == null || img.getHeight() <= 0) return getWidth();
            double imgRatio = img.getWidth() / img.getHeight();
            double paneRatio = getWidth() / (getHeight() <= 0 ? 1 : getHeight());
            return (paneRatio > imgRatio) ? getHeight() * imgRatio : getWidth();
        }, widthProperty(), heightProperty(), imageView.imageProperty());
    }

    private DoubleBinding getTrueHeight() {
        return Bindings.createDoubleBinding(() -> {
            Image img = imageView.getImage();
            if (img == null || img.getWidth() <= 0) return getHeight();
            double imgRatio = img.getWidth() / img.getHeight();
            double paneRatio = getWidth() / (getHeight() <= 0 ? 1 : getHeight());
            return (paneRatio > imgRatio) ? getHeight() : getWidth() / imgRatio;
        }, widthProperty(), heightProperty(), imageView.imageProperty());
    }

    public void setCenterPercentage(double xPercent, double yPercent) {
        this.centerSlot = new Point2D(xPercent, yPercent);
        rebindTotemLayout();
    }

    public void setTotem(TotemPieceView totemPieceView) {
        this.getChildren().removeIf(node -> node instanceof Pane);

        Pane layer = new Pane();
        this.currentTotemView = totemPieceView;

        this.currentTotemView.fitHeightProperty().bind(getTrueHeight().multiply(0.25));
        this.currentTotemView.setPreserveRatio(true);

        rebindTotemLayout();

        layer.getChildren().add(this.currentTotemView);
        this.getChildren().add(layer);

        if (this.currentTotemView.getPlayerName() != null) {
            setupPlayerNamePopup();
        }
    }

    private void rebindTotemLayout() {
        if (this.currentTotemView != null) {
            DoubleBinding trueW = getTrueWidth();
            DoubleBinding trueH = getTrueHeight();
            DoubleBinding offsetX = widthProperty().subtract(trueW).divide(2);
            DoubleBinding offsetY = heightProperty().subtract(trueH).divide(2);

            DoubleBinding halfWidth = this.currentTotemView.fitHeightProperty().multiply(0.35);
            DoubleBinding halfHeight = this.currentTotemView.fitHeightProperty().multiply(0.5);

            this.currentTotemView.translateXProperty().bind(
                    offsetX.add(trueW.multiply(centerSlot.getX())).subtract(halfWidth)
            );
            this.currentTotemView.translateYProperty().bind(
                    offsetY.add(trueH.multiply(centerSlot.getY())).subtract(halfHeight)
            );
        }
    }

    private void setupPlayerNamePopup() {
        if (this.currentTotemView == null) return;
        this.playerNamePopup = EffectsManager.createTotemPopup(currentTotemView.getTotemType(), currentTotemView.getPlayerName());

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

    public TotemPieceView getTotem() {
        return this.currentTotemView;
    }
}