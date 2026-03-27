package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.characters.CharacterType;

import java.util.List;
import java.util.function.ToIntFunction;

public enum EndGameBuildingFunction implements ToIntFunction<Player> {

    COUNT_ARTISTS{
        @Override
        public int applyAsInt(Player player) {
            return player.getCharacterDeck().get(CharacterType.ARTIST).size() * 4;
        }
    },

    COUNT_BUILDERS{
        @Override
        public int applyAsInt(Player player) {
            return player.getCharacterDeck().get(CharacterType.BUILDER).size() * 4;
        }
    },

    COUNT_GATHERERS{
        @Override
        public int applyAsInt(Player player) {
            return player.getCharacterDeck().get(CharacterType.GATHERER).size() * 4;
        }
    },

    COUNT_HUNTERS{
        @Override
        public int applyAsInt(Player player) {
            return player.getCharacterDeck().get(CharacterType.HUNTER).size() * 3;
        }
    },

    COUNT_INVENTORS{
        @Override
        public int applyAsInt(Player player) {
            return player.getCharacterDeck().get(CharacterType.INVENTOR).size() * 2;
        }
    },

    COUNT_SETS{
        @Override
        public int applyAsInt(Player player) {
            return player.getCharacterDeck().values().stream().mapToInt(List::size).min().orElse(0) *6;
        }
    },

    COUNT_SHAMANS{
        @Override
        public int applyAsInt(Player player) {
            return player.getCharacterDeck().get(CharacterType.SHAMAN).size() * 4;
        }
    },

    DOUBLE_BUILDERS{
        @Override
        public int applyAsInt(Player player) {
            return player.getBuildersPrestige();
        }
    },

    FIXED_25{
        @Override
        public int applyAsInt(Player player) {
            return 25;
        }
    }



}
