package it.polimi.gc06.mesos.controller.commands;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.model.Color;

public enum Request implements ControllerCaller {

    OFFER_TRACK_REQUEST {
        /**
         * This method invokes the handler for placing a totem on the offer track.
         *
         * @param controller the game controller.
         * @param nickname the nickname of the player.
         * @param index the index of the chosen offer track tile.
         */
        @Override
        public void call(GameController controller, String nickname, int index) {
            controller.handleTotemOfferTilePlacement(nickname, index);
        }
    },
    TOP_CARD_REQUEST {
        /**
         * This method invokes the handler for picking a card from the top row.
         *
         * @param controller the game controller.
         * @param nickname the nickname of the player.
         * @param index the index of the card in the top row.
         */
        @Override
        public void call(GameController controller, String nickname, int index) {
            controller.handleCardPickTopRow(nickname, index);
        }
    },
    BOTTOM_CARD_REQUEST {
        /**
         * This method invokes the handler for picking a card from the bottom row.
         *
         * @param controller the game controller.
         * @param nickname the nickname of the player.
         * @param index the index of the card in the bottom row.
         */
        @Override
        public void call(GameController controller, String nickname, int index) {
            controller.handleCardPickBottomRow(nickname, index);
        }
    },
    TOP_BUILDING_REQUEST {
        /**
         * This method invokes the handler for picking a building from the top row.
         *
         * @param controller the game controller.
         * @param nickname the nickname of the player.
         * @param index the index of the building in the top row.
         */
        @Override
        public void call(GameController controller, String nickname, int index) {
            controller.handleBuildingPickTopRow(nickname, index);
        }
    },
    BOTTOM_BUILDING_REQUEST {
        /**
         * This method invokes the handler for picking a building from the bottom row.
         *
         * @param controller the game controller.
         * @param nickname the nickname of the player.
         * @param index the index of the building in the bottom row.
         */
        @Override
        public void call(GameController controller, String nickname, int index) {
            controller.handleBuildingPickBottomRow(nickname, index);
        }
    },
    CHOOSE_TOTEM_COLOR_REQUEST {
        /**
         * This method invokes the handler for choosing the player's totem color.
         *
         * @param controller the game controller.
         * @param nickname the nickname of the player.
         * @param index the index corresponding to the chosen color in the Color enum.
         */
        @Override
        public void call(GameController controller, String nickname, int index) {
            Color color = Color.values()[index];
            controller.handleChooseTotemColor(nickname, color);
        }
    },

    //in this case index is not used!
    SKIP_REQUEST {
        /**
         * This method invokes the handler to skip a pick action.
         *
         * @param controller the game controller.
         * @param nickname the nickname of the player skipping the action.
         * @param index NOT USED in this request.
         */
        @Override
        public void call(GameController controller, String nickname, int index) {
            controller.handlePickSkip(nickname);
        }
    }
}
