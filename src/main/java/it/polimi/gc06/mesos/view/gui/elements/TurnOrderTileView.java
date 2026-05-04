package it.polimi.gc06.mesos.view.gui.elements;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

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

    public void applyTotemColor(ImageView imageView, Totem totem) {
        if (totem.equals(Totem.NONE)) {
            return;
        }

        Image original = imageView.getImage();
        int w = (int) original.getWidth();
        int h = (int) original.getHeight();

        Color targetColor = totem.getTotemColor();

        WritableImage result = new WritableImage(w, h);
        javafx.scene.image.PixelReader reader = original.getPixelReader();
        javafx.scene.image.PixelWriter writer = result.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color pixel = reader.getColor(x, y);

                if (pixel.getRed() > 0.85 && pixel.getGreen() > 0.85 && pixel.getBlue() > 0.85) {
                    writer.setColor(x, y, new Color(
                            targetColor.getRed(),
                            targetColor.getGreen(),
                            targetColor.getBlue(),
                            pixel.getOpacity()
                    ));
                } else {
                    writer.setColor(x, y, pixel);
                }
            }
        }

        imageView.setImage(result);
        imageView.setEffect(null);
    }
}
