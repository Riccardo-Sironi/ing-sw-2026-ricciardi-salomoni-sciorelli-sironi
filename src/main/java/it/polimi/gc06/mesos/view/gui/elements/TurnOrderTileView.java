package it.polimi.gc06.mesos.view.gui.elements;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurnOrderTileView extends TileView {
    private ArrayList<TotemPieceView> totemPieces;
    private final int numPlayers;

    private static final Map<Integer, List<Point2D>> SLOTS = new HashMap<>();

    static {
        SLOTS.put(5, List.of(
                new Point2D(0.5, 0.074),
                new Point2D(0.5, 0.25),
                new Point2D(0.5, 0.42),
                new Point2D(0.5, 0.59),
                new Point2D(0.5, 0.767)
        ));

        //TODO: calculate the correct percentage to put the totem in the exact center of the tile
        SLOTS.put(4, List.of(
                new Point2D(0, 0),
                new Point2D(0, 0),
                new Point2D(0, 0),
                new Point2D(0, 0),
                new Point2D(0, 0)
        ));
        SLOTS.put(3, List.of(
                new Point2D(0, 0),
                new Point2D(0, 0),
                new Point2D(0, 0)
        ));
        SLOTS.put(2, List.of(
                new Point2D(0, 0),
                new Point2D(0, 0)
        ));
    }

    public TurnOrderTileView(Image image, int numPlayers) {
        super(image);
        this.numPlayers = numPlayers;
        this.totemPieces = new ArrayList<>();
        this.imageView.setPreserveRatio(true);
    }

    public void setTotemPieces(ArrayList<TotemPieceView> totemPieces) {
        this.totemPieces = totemPieces;
        setupTotems();
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

    private void setupTotems() {
        this.getChildren().removeIf(node -> node instanceof Pane);

        Pane wrapper = new Pane();
        this.getChildren().add(wrapper);

        DoubleBinding trueW = getTrueWidthBinding();
        DoubleBinding trueH = getTrueHeightBinding();

        wrapper.maxWidthProperty().bind(trueW);
        wrapper.maxHeightProperty().bind(trueH);
        wrapper.prefWidthProperty().bind(trueW);
        wrapper.prefHeightProperty().bind(trueH);

        if (!totemPieces.isEmpty() && SLOTS.containsKey(numPlayers)) {
            List<Point2D> points = SLOTS.get(numPlayers);

            for (int i = 0; i < totemPieces.size(); i++) {
                if (i >= points.size()) break;

                TotemPieceView t = totemPieces.get(i);
                Point2D p = points.get(i);

                t.fitHeightProperty().bind(trueH.multiply(0.25));

                double imageRatio = t.getImage().getWidth() / t.getImage().getHeight();
                DoubleBinding totemInstantWidth = t.fitHeightProperty().multiply(imageRatio);

                t.layoutXProperty().bind(trueW.multiply(p.getX()).subtract(totemInstantWidth.divide(2)));
                t.layoutYProperty().bind(trueH.multiply(p.getY()).subtract(t.fitHeightProperty().divide(2)));

                wrapper.getChildren().add(t);
            }
        }
    }
}