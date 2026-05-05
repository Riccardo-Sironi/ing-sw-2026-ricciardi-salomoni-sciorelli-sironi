package it.polimi.gc06.mesos.view.gui.elements;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class CardView extends ImageView {

    public CardView(Image image) {
        super(image);
        this.setPreserveRatio(true);
        this.setSmooth(true);
    }
}
