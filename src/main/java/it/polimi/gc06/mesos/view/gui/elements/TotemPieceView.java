package it.polimi.gc06.mesos.view.gui.elements;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;

public class TotemPieceView extends ImageView {
    private PlayerView player;

    // In TotemPieceView.java
    public TotemPieceView() {
        super(); // Use empty constructor
        // Load image via InputStream
        this.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream(Totem.NONE.getTotemStanding())));
        this.player = null;
        this.setPreserveRatio(true);
        this.setSmooth(true);
    }

    public TotemPieceView(PlayerView player) {
        super(); // Use empty constructor
        // Load image via InputStream
        this.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream(Totem.getTotem(player.getColor()).getTotemStanding())));
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
