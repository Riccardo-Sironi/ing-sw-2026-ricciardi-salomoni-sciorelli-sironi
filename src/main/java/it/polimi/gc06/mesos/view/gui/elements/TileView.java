package it.polimi.gc06.mesos.view.gui.elements;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public abstract class TileView extends StackPane {
    ImageView imageView;

    public TileView(Image image) {
        super();

        imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        this.getChildren().addAll(imageView);
    }

    public ImageView getImageView() {
        return this.imageView;
    }
}
