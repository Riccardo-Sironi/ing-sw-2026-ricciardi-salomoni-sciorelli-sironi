package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.characters.CharacterType;

import java.util.List;
import java.util.function.ToIntFunction;

public enum EndGameBuildingFunction implements ToIntFunction<Player> {

    COUNT_ARTISTS{
        /**
         * this method is used to calculate the prestige gained by the card at the end of the game,
         * based on the number of artists in the player's character deck multiplied by 4.
         *
         * @param player the function argument
         * @return the prestige gained by the card at the end of the game,
         * calculated as 4 times the number of artists in the player's character deck.
         */
        @Override
        public int applyAsInt(Player player) {
            return player.getCharacterDeck().get(CharacterType.ARTIST).size() * 4;
        }
    },

    COUNT_BUILDERS{
        /**
         * this method is used to calculate the prestige gained by the card at the end of the game,
         * based on the number of builders in the player's character deck multiplied by 4.
         *
         * @param player the function argument
         * @return the prestige gained by the card at the end of the game,
         * calculated as 4 times the number of builders in the player's character deck.
         */
        @Override
        public int applyAsInt(Player player) {
            return player.getCharacterDeck().get(CharacterType.BUILDER).size() * 4;
        }
    },

    COUNT_GATHERERS{
        /**
         * this method is used to calculate the prestige gained by the card at the end of the game,
         * based on the number of gatherers in the player's character deck multiplied by 4.
         *
         * @param player the function argument
         * @return the prestige gained by the card at the end of the game,
         * calculated as 4 times the number of gatherers in the player's character deck.
         */
        @Override
        public int applyAsInt(Player player) {
            return player.getCharacterDeck().get(CharacterType.GATHERER).size() * 4;
        }
    },

    COUNT_HUNTERS{
        /**
         * this method is used to calculate the prestige gained by the card at the end of the game,
         * based on the number of hunters in the player's character deck multiplied by 3.
         *
         * @param player the function argument
         * @return the prestige gained by the card at the end of the game,
         * calculated as 3 times the number of hunters in the player's character deck.
         */
        @Override
        public int applyAsInt(Player player) {
            return player.getCharacterDeck().get(CharacterType.HUNTER).size() * 3;
        }
    },

    COUNT_INVENTORS{
        /**
         * this method is used to calculate the prestige gained by the card at the end of the game,
         * based on the number of inventors in the player's character deck multiplied by 2.
         *
         * @param player the function argument
         * @return the prestige gained by the card at the end of the game,
         * calculated as 2 times the number of inventors in the player's character deck.
         */
        @Override
        public int applyAsInt(Player player) {
            return player.getCharacterDeck().get(CharacterType.INVENTOR).size() * 2;
        }
    },

    /**
     * this method is used to calculate the prestige gained by the card at the end of the game,
     * based on the number of sets ( = having at least one card of each character type)
     * in the player's character deck multiplied by 6.
     *
     * @param player the function argument
     * @return the prestige gained by the card at the end of the game,
     * calculated as 6 times the number of sets in the player's character deck.
     */
    COUNT_SETS{
        @Override
        public int applyAsInt(Player player) {
            return player.getCharacterDeck().values().stream().mapToInt(List::size).min().orElse(0) *6;
        }
    },

    COUNT_SHAMANS{
        /**
         * this method is used to calculate the prestige gained by the card at the end of the game,
         * based on the number of shamans in the player's character deck multiplied by 4.
         *
         * @param player the function argument
         * @return the prestige gained by the card at the end of the game,
         * calculated as 4 times the number of shamans in the player's character deck.
         */
        @Override
        public int applyAsInt(Player player) {
            return player.getCharacterDeck().get(CharacterType.SHAMAN).size() * 4;
        }
    },

    DOUBLE_BUILDERS{
        /**
         * this method is used to calculate the prestige gained by the card at the end of the game,
         * based on the number of prestige gained by each builder multiplied by 2.
         *
         * @param player the function argument
         * @return the prestige gained by the card at the end of the game,
         * calculated as 2 times the prestige gained by each builder in the player's character deck.
         */
        @Override
        public int applyAsInt(Player player) {
            return player.getBuildersPrestige();
        }
    },

    /**
     * this method is used to calculate the prestige gained by the card at the end of the game,
     * adds 25 prestige to the player who owns it
     *
     * @param player the player who owns the card, function argument
     * @return 25 prestige
     */
    FIXED_25{
        @Override
        public int applyAsInt(Player player) {
            return 25;
        }
    }



}
