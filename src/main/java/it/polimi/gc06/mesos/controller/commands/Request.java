package it.polimi.gc06.mesos.controller.commands;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.model.Color;

public enum Request implements ControllerCaller {

    OFFER_TRACK_REQUEST {
        @Override
        public void call(GameController controller, String nickname, int index) {
            controller.handleTotemOfferTilePlacement(nickname, index);
        }
    },
    TOP_CARD_REQUEST {
        @Override
        public void call(GameController controller, String nickname, int index) {
            controller.handleCardPickTopRow(nickname, index);
        }
    },
    BOTTOM_CARD_REQUEST {
        @Override
        public void call(GameController controller, String nickname, int index) {
            controller.handleCardPickBottomRow(nickname, index);
        }
    },
    TOP_BUILDING_REQUEST {
        @Override
        public void call(GameController controller, String nickname, int index) {
            controller.handleBuildingPickTopRow(nickname, index);
        }
    },
    BOTTOM_BUILDING_REQUEST {
        @Override
        public void call(GameController controller, String nickname, int index) {
            controller.handleBuildingPickBottomRow(nickname, index);
        }
    },
    CHOOSE_TOTEM_COLOR_REQUEST {
        @Override
        public void call(GameController controller, String nickname, int index) {
            Color color = Color.values()[index];
            controller.handleChooseTotemColor(nickname, color);
        }
    },

    //in this case index is not used!
    SKIP_REQUEST {
        @Override
        public void call(GameController controller, String nickname, int index) {
            controller.handlePickSkip(nickname);
        }
    }
}
