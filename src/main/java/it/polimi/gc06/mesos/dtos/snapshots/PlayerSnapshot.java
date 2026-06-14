package it.polimi.gc06.mesos.dtos.snapshots;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterType;
import it.polimi.gc06.mesos.model.cards.characters.InventionIcon;

import java.util.List;
import java.util.Map;

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