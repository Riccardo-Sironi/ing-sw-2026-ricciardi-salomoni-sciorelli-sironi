package it.polimi.gc06.mesos.view.gui.elements;

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
}
