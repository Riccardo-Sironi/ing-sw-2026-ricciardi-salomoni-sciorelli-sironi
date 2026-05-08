package it.polimi.gc06.mesos.view;

import it.polimi.gc06.mesos.network.socket.infos.CardPickedInfo;
import it.polimi.gc06.mesos.network.socket.infos.ResourcesInfo;
import it.polimi.gc06.mesos.network.socket.infos.TotemMovedInfo;

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
     * Refill info should also update deck size, returns the array of cards.
     * */
    TOP_ROW_REFILL,
    TOP_BUILDINGS_REFILL,

    //player info
    PLAYER_CAN_SKIP, /**returns the {@link SkipRightInfo}*/
    /** they all return the {@link ResourcesInfo}*/
    FOOD_CHANGED,
    PRESTIGE_CHANGED,
    TOP_NUM_DRAW_CHANGED,
    BOTTOM_NUM_DRAW_CHANGED,

    //miscellaneous
    TOTEM_MOVED_OFFER, /**returns the {@link TotemMovedInfo}*/
    TOTEM_PLACEMENT_TURN, /**returns the {@link TotemMovedInfo}, sent at the star of the game*/
    PHASE_CHANGED, /**returns the new phase as String*/
    ACTIVE_PLAYER_CHANGED, /**returns the nickname of the new active player*/
    ROUND_CHANGED, /**returns the new round as integer*/
    ERA_CHANGED, /**client-side all buildings should be moved to bottom on this notify, returns the Era as String*/
    IS_END_GAME /** returns the leaderboard*/
}