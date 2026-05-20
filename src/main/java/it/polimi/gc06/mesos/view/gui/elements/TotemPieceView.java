package it.polimi.gc06.mesos.view.gui.elements;

import it.polimi.gc06.mesos.view.gui.ImageFetcher;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;

import static it.polimi.gc06.mesos.view.gui.GUI.imageFetcher;

public class TotemPieceView extends ImageView {
    private PlayerView player;

    // In TotemPieceView.java
    public TotemPieceView() {
        super();
        this.setImage(imageFetcher.getTotemImage());
        this.player = null;
        this.setPreserveRatio(true);
        this.setSmooth(true);
    }

    public TotemPieceView(PlayerView player) {
        super();
        this.setImage(imageFetcher.getTotemImage(player.getColor()));
        this.player = player;
        this.setPreserveRatio(true);
        this.setSmooth(true);
        applyTotemEffect();
    }

    private void applyTotemEffect() {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(2.0);
        shadow.setOffsetX(-3.0);
        shadow.setOffsetY(3);
        shadow.setColor(javafx.scene.paint.Color.color(0, 0, 0, 0.5));
        this.setEffect(shadow);
    }

    public PlayerView getPlayer() {
        return player;
    }

    public void setPlayer(PlayerView player) {
        this.player = player;
        if (this.player != null) {
            applyTotemEffect();
        }
    }
}
