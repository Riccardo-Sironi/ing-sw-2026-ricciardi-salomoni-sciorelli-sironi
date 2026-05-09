package it.polimi.gc06.mesos.view.gui.elements;

import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class TotemPieceView extends ImageView {
    private Totem totemType;
    private String playerName;

    public TotemPieceView(Totem totem, String playerName) {
        super(totem.getTotemStanding());
        this.totemType = totem;
        this.playerName = playerName;
        this.setPreserveRatio(true);
        this.setSmooth(true);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(2.0);
        shadow.setOffsetX(-3.0);
        shadow.setOffsetY(3);
        shadow.setColor(Color.color(0, 0, 0, 0.5));
        this.setEffect(shadow);
    }

    public void setTotemType(Totem totemType) {
        this.totemType = totemType;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Totem getTotemType() {
        return totemType;
    }

    public String getPlayerName() {
        return playerName;
    }
}
