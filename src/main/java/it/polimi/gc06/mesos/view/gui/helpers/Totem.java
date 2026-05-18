package it.polimi.gc06.mesos.view.gui.helpers;

import it.polimi.gc06.mesos.model.Color;

public enum Totem {
    ORANGE,
    TURQUOISE,
    WHITE,
    YELLOW,
    PURPLE,
    NONE;

    public String getTotemStanding() {
        return "/imgs/totems/" + this.name().toLowerCase() + "_standing_totem.png";
    }

    public String getTotemTurnOrderOverlay() {
        return "/imgs/totems/" + this.name().toLowerCase() + "_totem_overlay.png";
    }

    public static Totem getTotem(Color color) {
        switch (color) {
            case RED:
                return Totem.ORANGE;
            case BLUE:
                return Totem.TURQUOISE;
            case WHITE:
                return Totem.WHITE;
            case YELLOW:
                return Totem.YELLOW;
            case PURPLE:
                return Totem.PURPLE;
            default:
                return Totem.NONE;
        }
    }

    public javafx.scene.paint.Color getTotemColor() {
        return switch (this) {
            case ORANGE -> javafx.scene.paint.Color.web("D3523B");
            case TURQUOISE -> javafx.scene.paint.Color.web("208FA5");
            case WHITE -> javafx.scene.paint.Color.web("E6E6E1");
            case YELLOW -> javafx.scene.paint.Color.web("F0C135");
            case PURPLE -> javafx.scene.paint.Color.web("2D1220");
            case NONE -> javafx.scene.paint.Color.GREY;
            default -> null;
        };
    }

    public String getTotemColorHex() {
        javafx.scene.paint.Color c = getTotemColor();
        return String.format("#%02X%02X%02X",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255)
        );
    }

    public String getTotemColorRGB() {
        return switch (this) {
            case ORANGE -> "211, 82, 59";
            case TURQUOISE -> "32, 143, 165";
            case WHITE -> "230, 230, 225";
            case YELLOW -> "240, 193, 53";
            case PURPLE -> "45, 18, 32";
            case NONE -> "128, 128, 128";
            default -> null;
        };
    }

    public String getTotemColorRGBbrighter() {
        return switch (this) {
            case ORANGE -> "255, 120, 90";
            case TURQUOISE -> "70, 180, 200";
            case WHITE -> "255, 255, 255";
            case YELLOW -> "255, 220, 100";
            case PURPLE -> "80, 50, 70";
            case NONE -> "160, 160, 160";
            default -> null;
        };
    }
}
