package it.polimi.gc06.mesos.view.gui.elements;

import it.polimi.gc06.mesos.view.gui.helpers.EffectsManager;
import it.polimi.gc06.mesos.view.gui.helpers.TurnOrderTileInfo;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;

import java.util.ArrayList;
import java.util.List;

public class TurnOrderTileView extends TileView {
    private ArrayList<TotemPieceView> totemPieces;
    private final int numPlayers;

    public TurnOrderTileView(Image image, int numPlayers) {
        super(image);
        this.numPlayers = numPlayers;
        this.totemPieces = new ArrayList<>();
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

    public void setTotemPieces(ArrayList<TotemPieceView> totemPieces) {
        this.totemPieces = totemPieces;
        setupTotems();
    }

    private void setupTotems() {
        this.getChildren().removeIf(node -> node instanceof Pane);

        Pane layer = new Pane();
        this.getChildren().add(layer);

        TurnOrderTileInfo slots = TurnOrderTileInfo.getInfo(numPlayers);
        if (totemPieces != null && !totemPieces.isEmpty() && slots != null) {
            List<Point2D> points = slots.getPoints();

            DoubleBinding trueW = getTrueWidth();
            DoubleBinding trueH = getTrueHeight();
            DoubleBinding offsetX = widthProperty().subtract(trueW).divide(2);
            DoubleBinding offsetY = heightProperty().subtract(trueH).divide(2);

            for (int i = 0; i < totemPieces.size(); i++) {
                if (i >= points.size()) break;

                TotemPieceView t = totemPieces.get(i);
                setupPlayerNamePopup(t);
                Point2D p = points.get(i);

                t.fitHeightProperty().bind(trueH.multiply(0.25));
                t.setPreserveRatio(true);

                DoubleBinding halfWidth = t.fitHeightProperty().multiply(0.35); // Approx 70% aspect ratio
                DoubleBinding halfHeight = t.fitHeightProperty().multiply(0.5);

                t.translateXProperty().bind(
                        offsetX.add(trueW.multiply(p.getX())).subtract(halfWidth)
                );
                t.translateYProperty().bind(
                        offsetY.add(trueH.multiply(p.getY())).subtract(halfHeight)
                );

                layer.getChildren().add(t);
            }
        }
    }

    private void setupPlayerNamePopup(TotemPieceView totemPiece) {
        if (totemPiece == null) return;
        Popup popup = EffectsManager.createTotemPopup(totemPiece.getTotemType(), totemPiece.getPlayerName());

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
}