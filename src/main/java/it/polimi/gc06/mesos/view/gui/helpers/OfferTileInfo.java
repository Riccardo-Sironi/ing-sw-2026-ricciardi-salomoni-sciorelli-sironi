package it.polimi.gc06.mesos.view.gui.helpers;

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
            case OFFER_TILE_A, OFFER_TILE_B,
                 OFFER_TILE_D, OFFER_TILE_E,
                 OFFER_TILE_F, OFFER_TILE_G -> 0.53;
            case OFFER_TILE_C -> 0.52;
        };
    }

    public double getYPercent() {
        return 0.2;
    }
}
