package it.polimi.gc06.mesos.dtos.snapshots;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterType;
import it.polimi.gc06.mesos.model.cards.characters.InventionIcon;

import java.util.List;
import java.util.Map;

/**
 * Represents a static snapshot of a player's state, including resources and cards.
 *
 * @param nickname The player's nickname.
 * @param color The color associated with the player.
 * @param prestigeTokens The current amount of prestige tokens the player has.
 * @param foodTokens The current amount of food tokens the player has.
 * @param shamanStars The number of shaman stars the player has collected.
 * @param topDrawNum The number of draws the player is allowed from the top row.
 * @param bottomDrawNum The number of draws the player is allowed from the bottom row.
 * @param characterDeck A map categorizing the character cards owned by the player.
 * @param buildingDeck The list of building cards owned by the player.
 * @param charactersSets A map tracking the number of character sets completed.
 * @param inventorPairs A map tracking the pairs of inventor cards collected.
 */
public record PlayerSnapshot(
        String nickname,
        Color color,
        int prestigeTokens,
        int foodTokens,
        int shamanStars,
        int topDrawNum,
        int bottomDrawNum,
        Map<CharacterType, List<CharacterCard>> characterDeck,
        List<BuildingCard> buildingDeck,
        Map<CharacterType, Integer> charactersSets,
        Map<InventionIcon, Integer> inventorPairs
) {}