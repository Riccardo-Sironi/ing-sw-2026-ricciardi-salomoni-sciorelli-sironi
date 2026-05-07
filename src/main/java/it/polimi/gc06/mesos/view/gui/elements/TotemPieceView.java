package it.polimi.gc06.mesos.view.gui.elements;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class TotemPieceView extends ImageView {
    public TotemPieceView(Totem totem) {
        super(totem.getTotemStanding());
        this.setPreserveRatio(true);
        this.setSmooth(true);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(2.0);
        shadow.setOffsetX(-3.0);
        shadow.setOffsetY(3);
        shadow.setColor(Color.color(0, 0, 0, 0.5));
        this.setEffect(shadow);
    }
}
