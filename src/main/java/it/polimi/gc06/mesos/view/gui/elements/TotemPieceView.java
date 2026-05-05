package it.polimi.gc06.mesos.view.gui.elements;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TotemPieceView extends ImageView {
    Totem totem;
    String playerName;

    public TotemPieceView(Totem totem) {
        super(totem.getTotemStanding());
        this.setPreserveRatio(true);
        this.setSmooth(true);
    }

    public Totem getTotem() {
        return totem;
    }

    public void setTotem(Totem totem) {
        this.totem = totem;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
