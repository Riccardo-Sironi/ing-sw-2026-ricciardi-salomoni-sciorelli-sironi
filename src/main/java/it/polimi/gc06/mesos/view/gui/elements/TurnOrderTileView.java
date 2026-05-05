package it.polimi.gc06.mesos.view.gui.elements;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;

public class TurnOrderTileView extends TileView {

    private ArrayList<TotemPieceView> totemPieces;

    private static final double TOTEM_OVERLAP = 40;         // px between each totem
    private static final double TOTEM_HEIGHT = 85;          // approx rendered height of one totem
    private static final double TOTEM_WIDTH = 100;          // wider than totem image to allow centering
    private static final double TOTEM_START_RATIO = 0.10;   // first totem at 10% of tile height
    private static final double TOTEM_STEP_RATIO = 0.11;    // each next totem 11% lower

    public TurnOrderTileView(Image image) {
        super(image);
        totemPieces = new ArrayList<>();
    }

    public TurnOrderTileView(Image image, ArrayList<TotemPieceView> totemPieces) {
        super(image);
        this.totemPieces = totemPieces;
        setupTotems();
    }

    private void setupTotems() {
        this.getChildren().clear();
        this.getChildren().add(imageView);

        if (totemPieces.isEmpty()) return;

        Pane totemStack = new Pane();

        for (int i = 0; i < totemPieces.size(); i++) {
            TotemPieceView totemPiece = totemPieces.get(i);
            totemPiece.layoutXProperty().bind(
                    totemStack.widthProperty()
                            .subtract(totemPiece.fitWidthProperty())
                            .divide(2)
            );
            totemPiece.fitWidthProperty().bind(this.prefWidthProperty().multiply(0.32));
            totemPiece.layoutYProperty().bind(
                    this.heightProperty().multiply(TOTEM_START_RATIO + i * TOTEM_STEP_RATIO)
            );
            totemStack.getChildren().add(totemPiece);
        }

        double totalHeight = (totemPieces.size() - 1) * TOTEM_OVERLAP + TOTEM_HEIGHT;
        totemStack.setPrefSize(TOTEM_WIDTH, totalHeight);

        StackPane.setAlignment(totemStack, Pos.TOP_CENTER);

        this.getChildren().add(totemStack);
    }

    public ArrayList<TotemPieceView> getTotemPieces() {
        return totemPieces;
    }

    public void setTotemPieces(ArrayList<TotemPieceView> totemPieces) {
        this.totemPieces = totemPieces;
        setupTotems();
    }
}