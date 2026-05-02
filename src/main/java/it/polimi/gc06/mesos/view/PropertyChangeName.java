package it.polimi.gc06.mesos.view;

import it.polimi.gc06.mesos.network.socket.infos.CardPickedInfo;
import java.util.function.Supplier;

public enum PropertyChangeName implements Supplier<String> {

    /**
     * For card and building picking it is specified the position, the card and the player who picked it
     * Reconstruct self and opponents view with this notify.
     * See info {@link CardPickedInfo}
     * */
    PICK_FROM_TOP_ROW {
        @Override
        public String get() {
            return "pickFromTopRow";
        }
    },
    PICK_FROM_BOTTOM_ROW {
        @Override
        public String get() {
            return "pickFromBottomRow";
        }
    },
    PICK_FROM_TOP_BUILDINGS {
        @Override
        public String get() {
            return "pickFromTopBuildings";
        }
    },
    PICK_FROM_BOTTOM_BUILDINGS {
        @Override
        public String get() {
            return "pickFromBottomBuildings";
        }
    },

    /**
     * Refill info should also update deck size
     * */
    TOP_ROW_REFILL {
        @Override
        public String get() {
            return "topRowRefill";
        }
    },
    TOP_BUILDING_REFILL {
        @Override
        public String get() {
            return "topBuildingRefill";
        }
    },

    /**
     * Miscellaneous
     * */
    TOTEM_MOVED {
        @Override
        public String get() {
            return "totemMoved";
        }
    },
    PHASE_CHANGED {
        @Override
        public String get() {
            return "phaseChanged";
        }
    },
    ACTIVE_PLAYER_CHANGED {
        @Override
        public String get() {
            return "activePlayerChanged";
        }
    },
    ROUND_CHANGED {
        @Override
        public String get() {
            return "roundChanged";
        }
    },
    IS_END_GAME {
        @Override
        public String get() {
            return "isEndGame";
        }
    }
}