package it.polimi.gc06.mesos.view.gui.elements;

import it.polimi.gc06.mesos.view.gui.helpers.EffectsManager;
import it.polimi.gc06.mesos.view.gui.helpers.Totem;
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

    /**
     * Returns a binding for the true width of the tile; this method is used to help place the Totem in the correct position.
     *
     * @return the {@link DoubleBinding} representing the true width of the tile
     */
    private DoubleBinding getTrueWidth() {
        return Bindings.createDoubleBinding(() -> {
            Image img = imageView.getImage();
            if (img == null || img.getHeight() <= 0) return getWidth();
            double imgRatio = img.getWidth() / img.getHeight();
            double paneRatio = getWidth() / (getHeight() <= 0 ? 1 : getHeight());
            return (paneRatio > imgRatio) ? getHeight() * imgRatio : getWidth();
        }, widthProperty(), heightProperty(), imageView.imageProperty());
    }

    /**
     * Returns a binding for the true height of the tile; this method is used to help place the Totem in the correct position.
     *
     * @return the {@link DoubleBinding} representing the true height of the tile
     */
    private DoubleBinding getTrueHeight() {
        return Bindings.createDoubleBinding(() -> {
            Image img = imageView.getImage();
            if (img == null || img.getWidth() <= 0) return getHeight();
            double imgRatio = img.getWidth() / img.getHeight();
            double paneRatio = getWidth() / (getHeight() <= 0 ? 1 : getHeight());
            return (paneRatio > imgRatio) ? getHeight() : getWidth() / imgRatio;
        }, widthProperty(), heightProperty(), imageView.imageProperty());
    }

    /**
     * Set the center position of the Totem in percentage of the tile's true width and height.
     *
     * @param xPercent the x-coordinate of the center position as a percentage
     * @param yPercent the y-coordinate of the center position as a percentage
     */
    public void setCenterPercentage(double xPercent, double yPercent) {
        this.centerSlot = new Point2D(xPercent, yPercent);
        rebindTotemLayout();
    }

    /**
     * Set the TotemPieceView to be displayed on the tile. This method removes any existing TotemPieceView and adds the
     * new one, binding its size and position to the tile's dimensions.
     *
     * @param totemPieceView the {@link TotemPieceView} object to be set on the tile.
     */
    public void setTotem(TotemPieceView totemPieceView) {
        this.getChildren().removeIf(node -> node instanceof Pane);

        Pane layer = new Pane();
        this.currentTotemView = totemPieceView;

        this.currentTotemView.fitHeightProperty().unbind();
        this.currentTotemView.fitHeightProperty().bind(getTrueHeight().multiply(0.25));
        this.currentTotemView.setPreserveRatio(true);

        rebindTotemLayout();

        layer.getChildren().add(this.currentTotemView);
        this.getChildren().add(layer);

        if (this.currentTotemView.getPlayer() != null) {
            setupPlayerNamePopup();
        }
    }

    /**
     * Rebinds the layout of the TotemPieceView to the tile's dimensions.
     */
    private void rebindTotemLayout() {
        if (this.currentTotemView != null) {
            DoubleBinding trueW = getTrueWidth();
            DoubleBinding trueH = getTrueHeight();
            DoubleBinding offsetX = widthProperty().subtract(trueW).divide(2);
            DoubleBinding offsetY = heightProperty().subtract(trueH).divide(2);

            DoubleBinding halfWidth = this.currentTotemView.fitHeightProperty().multiply(0.35);
            DoubleBinding halfHeight = this.currentTotemView.fitHeightProperty().multiply(0.5);

            this.currentTotemView.translateXProperty().unbind();
            this.currentTotemView.translateYProperty().unbind();

            this.currentTotemView.translateXProperty().bind(
                    offsetX.add(trueW.multiply(centerSlot.getX())).subtract(halfWidth)
            );
            this.currentTotemView.translateYProperty().bind(
                    offsetY.add(trueH.multiply(centerSlot.getY())).subtract(halfHeight)
            );
        }
    }

    /**
     * Sets up a popup that displays the player's name when hovering over the TotemPieceView. The popup is shown when
     * the mouse enters the TotemPieceView, follows the mouse movement, and is hidden when the mouse exits.
     */
    private void setupPlayerNamePopup() {
        if (this.currentTotemView == null) return;
        this.playerNamePopup = EffectsManager.createTotemPopup(Totem.getTotem(currentTotemView.getPlayer().getColor()), currentTotemView.getPlayer().getNickname());

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

    /**
     * Get the current TotemPieceView displayed on the tile.
     *
     * @return the {@link TotemPieceView} currently displayed on the tile, or null if no TotemPieceView is set.
     */
    public TotemPieceView getTotem() {
        return this.currentTotemView;
    }
}