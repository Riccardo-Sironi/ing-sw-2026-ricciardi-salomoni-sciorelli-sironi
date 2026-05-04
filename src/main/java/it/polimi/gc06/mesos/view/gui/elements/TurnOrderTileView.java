package it.polimi.gc06.mesos.view.gui.elements;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class TurnOrderTileView extends TileView {

    ArrayList<ImageView> totemOverlays;
    ArrayList<Totem> totems;
    ArrayList<String> playerNames;

    public TurnOrderTileView(Image image) {
        super(image);
        totemOverlays = new ArrayList<>();
        playerNames = new ArrayList<>();
        totems = new ArrayList<>();
    }

    public ArrayList<ImageView> getTotemOverlays() {
        return totemOverlays;
    }

    public ArrayList<String> getPlayerNames() {
        return playerNames;
    }

    public ArrayList<Totem> getTotems() {
        return totems;
    }
}
