package it.polimi.gc06.mesos.view.gui.elements;

import it.polimi.gc06.mesos.view.gui.helpers.EffectsManager;
import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import it.polimi.gc06.mesos.view.gui.helpers.TurnOrderTileInfo;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Popup;

import java.util.ArrayList;
import java.util.List;

public class TurnOrderTileView extends TileView {
    private ArrayList<TotemPieceView> totemPieces;

    public TurnOrderTileView(Image image) {
        super(image);

        this.totemPieces = new ArrayList<>();
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
     * Set the list of TotemPieceView objects to be displayed on the tile and update their positions accordingly.
     *
     * @param totemPieces the {@link ArrayList} of {@link TotemPieceView} objects to be displayed on the tile
     */
    public void setTotemPieces(ArrayList<TotemPieceView> totemPieces) {
        this.totemPieces = totemPieces;
        setupTotems();
    }

    /**
     * Set up the TotemPieceView objects on the tile based on their positions defined in TurnOrderTileInfo.
     * This method removes any existing TotemPieceView objects and adds the new ones, binding their layout properties
     * to the true width and height of the tile.
     */
    private void setupTotems() {
        this.getChildren().removeIf(node -> node instanceof Pane);

        Pane layer = new Pane();

        DoubleBinding trueW = getTrueWidth();
        DoubleBinding trueH = getTrueHeight();

        layer.prefWidthProperty().bind(trueW);
        layer.prefHeightProperty().bind(trueH);
        layer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        this.getChildren().add(layer);

        TurnOrderTileInfo slots = TurnOrderTileInfo.getInfo(totemPieces.size());
        if (!totemPieces.isEmpty() && slots != null) {
            List<Point2D> points = slots.getPoints();

            for (int i = 0; i < totemPieces.size(); i++) {
                if (i >= points.size()) break;
                if (totemPieces.get(i).getPlayer() == null) continue;

                TotemPieceView t = totemPieces.get(i);
                setupPlayerNamePopup(t);

                Point2D p = points.get(i);

                t.fitHeightProperty().bind(trueH.multiply(0.25));
                t.setPreserveRatio(true);

                DoubleBinding halfWidth = t.fitHeightProperty().multiply(0.35);
                DoubleBinding halfHeight = t.fitHeightProperty().multiply(0.5);

                t.layoutXProperty().bind(trueW.multiply(p.getX()).subtract(halfWidth));
                t.layoutYProperty().bind(trueH.multiply(p.getY()).subtract(halfHeight));

                t.applyTotemEffect();

                layer.getChildren().add(t);
            }
        }
    }

    /**
     * Set up the player name popup for the given TotemPieceView.
     * This method creates a popup that displays the player's nickname and totem type when the mouse hovers over the
     * TotemPieceView.
     *
     * @param totemPiece the {@link TotemPieceView} for which the popup is to be set up.
     */
    private void setupPlayerNamePopup(TotemPieceView totemPiece) {
        Totem totemType = Totem.getTotem(totemPiece.getPlayer().getColor());
        if (totemType == Totem.NONE) return;
        Popup popup = EffectsManager.createTotemPopup(totemType, totemPiece.getPlayer().getNickname());

        totemPiece.setOnMouseEntered((event) -> {
            popup.show(totemPiece, event.getScreenX(), event.getScreenY());
            EffectsManager.playPopupIn(popup);
        });
        totemPiece.setOnMouseMoved((event) -> {
            popup.setX(event.getScreenX() - 35);
            popup.setY(event.getScreenY() - 60);
        });
        totemPiece.setOnMouseExited((event) -> {
            EffectsManager.playPopupOut(popup);
        });
    }

    /**
     * Get the totem pieces currently displayed on the tile.
     *
     * @return the {@link ArrayList} of {@link TotemPieceView} objects currently displayed on the tile
     */
    public ArrayList<TotemPieceView> getTotemPieces() {
        return totemPieces;
    }
}