package it.polimi.gc06.mesos.view.gui.elements;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class TurnOrderTileView extends TileView {

    private ArrayList<TotemPieceView> totemPieces;
    //private VBox totemPiecesContainer;

    public TurnOrderTileView(Image image, ArrayList<TotemPieceView> totemPieces) {
        super(image);
        this.totemPieces = totemPieces;

//        totemPiecesContainer = new VBox();
//        totemPiecesContainer.setAlignment(Pos.CENTER);
//        totemPiecesContainer.setSpacing(4);


//        totemPiecesContainer.prefWidthProperty().bind(this.prefWidthProperty());
//        totemPiecesContainer.prefHeightProperty().bind(this.prefHeightProperty());
//
//        for (TotemPieceView totemPiece : totemPieces) {
//            totemPiece.fitWidthProperty().bind(
//                    this.prefWidthProperty().multiply(0.45)
//                            .divide(Math.max(1, totemPieces.size() / 2.0))
//            );
//            totemPiece.translateYProperty().bind(
//                    this.prefHeightProperty().multiply(0.1)
//                            .multiply(totemPieces.indexOf(totemPiece) / 2.0));
//            totemPiecesContainer.getChildren().add(totemPiece);
//        }
//
//        this.getChildren().add(totemPiecesContainer);
//        StackPane.setAlignment(totemPiecesContainer, Pos.CENTER);
    }

    public ArrayList<TotemPieceView> getTotemPieces() {
        return totemPieces;
    }

    public void setTotemPieces(ArrayList<TotemPieceView> totemPieces) {
        this.totemPieces = totemPieces;
    }
}
