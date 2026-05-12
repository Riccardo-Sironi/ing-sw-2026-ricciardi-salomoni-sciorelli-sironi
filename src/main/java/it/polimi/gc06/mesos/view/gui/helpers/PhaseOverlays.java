package it.polimi.gc06.mesos.view.gui.helpers;

public enum PhaseOverlays {
    TOTEM_PHASE,
    OFFER_PHASE,
    EVENT_PHASE,
    END_PHASE;

    public static PhaseOverlays getPhase(String phase) {
        return switch (phase) {
            case "placing_totem" -> TOTEM_PHASE;
            case "offer_resolution" -> OFFER_PHASE;
            case "event_resolution" -> EVENT_PHASE;
            case "end_of_round" -> END_PHASE;
            default -> null;
        };
    }

    public String getOverlayPath() {
        return this.name().toLowerCase() + "_phase_overlay.png";
    }
}


