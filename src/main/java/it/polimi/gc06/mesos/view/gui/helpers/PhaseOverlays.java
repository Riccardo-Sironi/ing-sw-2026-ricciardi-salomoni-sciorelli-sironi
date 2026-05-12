package it.polimi.gc06.mesos.view.gui.helpers;

public enum PhaseOverlays {
    PLACING_TOTEM,
    OFFER_RESOLUTION,
    EVENT_RESOLUTION,
    END_OF_ROUND;

    public static PhaseOverlays getPhase(String phase) {
        return switch (phase) {
            case "placing_totem" -> PLACING_TOTEM;
            case "offer_resolution" -> OFFER_RESOLUTION;
            case "event_resolution" -> EVENT_RESOLUTION;
            case "end_of_round" -> END_OF_ROUND;
            default -> null;
        };
    }

    public String getOverlayPath() {
        return this.name().toLowerCase() + "_phase_overlay.png";
    }
}
