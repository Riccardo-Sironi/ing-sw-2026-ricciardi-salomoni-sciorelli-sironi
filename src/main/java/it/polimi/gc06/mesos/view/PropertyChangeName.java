package it.polimi.gc06.mesos.view;

import it.polimi.gc06.mesos.network.socket.infos.CardPickedInfo;
import java.util.function.Supplier;

public enum PropertyChangeName{

    /**
     * For card and building picking it is specified the position, the card and the player who picked it
     * Reconstruct self and opponents view with this notify.
     * See info {@link CardPickedInfo}
     * */
    PICK_FROM_TOP_ROW,
    PICK_FROM_BOTTOM_ROW,
    PICK_FROM_TOP_BUILDINGS,
    PICK_FROM_BOTTOM_BUILDINGS,

    /**
     * Refill info should also update deck size
     * */
    TOP_ROW_REFILL,
    TOP_BUILDING_REFILL,

    /**
     * Miscellaneous
     * */
    TOTEM_MOVED,
    PHASE_CHANGED,
    ACTIVE_PLAYER_CHANGED,
    ROUND_CHANGED,
    IS_END_GAME
}