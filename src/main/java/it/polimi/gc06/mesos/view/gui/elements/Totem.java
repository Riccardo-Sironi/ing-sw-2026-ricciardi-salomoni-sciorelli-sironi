package it.polimi.gc06.mesos.view.gui.elements;

import javafx.scene.paint.Color;

public enum Totem {
    ORANGE,
    TURQUOISE,
    WHITE,
    YELLOW,
    PURPLE,
    NONE;

    private String totemOverlay;

    public String getTotemOverlay() {
        return this.name().toLowerCase() + "_totem_overlay.png";
    }

    public String getTotemStanding() {
        return this.name().toLowerCase() + "_standing_totem.png";
    }

    public Color getTotemColor() {
        return switch (this) {
            case ORANGE -> Color.web("D3523B");
            case TURQUOISE -> Color.web("208FA5");
            case WHITE -> Color.web("E6E6E1");
            case YELLOW -> Color.web("F0C135");
            case PURPLE -> Color.web("2D1220");
            case NONE -> Color.GREY;
            default -> null;
        };
    }

    public String getTotemColorHex() {
        Color c = getTotemColor();
        return String.format("#%02X%02X%02X",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255)
        );
    }
}
