package it.polimi.gc06.mesos.view.gui.elements;

public enum OfferTileInfo {
    OFFER_TILE_A,
    OFFER_TILE_B,
    OFFER_TILE_C,
    OFFER_TILE_D,
    OFFER_TILE_E,
    OFFER_TILE_F,
    OFFER_TILE_G;

    public String getImagePath() {
        return this.name().toLowerCase() + ".png";
    }

    public double getXPercent() {
        return switch (this) {
            case OFFER_TILE_A -> 0.530;
            case OFFER_TILE_B -> 0.497;
            case OFFER_TILE_C -> 0.465;
            case OFFER_TILE_D -> 0.500;
            case OFFER_TILE_E -> 0.527;
            case OFFER_TILE_F -> 0.530;
            case OFFER_TILE_G -> 0.480;
        };
    }

    public double getYPercent() {
        return 0.2;
    }
}
