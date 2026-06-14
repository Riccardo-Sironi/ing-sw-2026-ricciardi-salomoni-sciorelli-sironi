package it.polimi.gc06.mesos.dtos.snapshots;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import java.util.List;
import java.util.Map;

public record BoardSnapshot(
        Era currentEra,
        boolean isEndGame,
        List<TribeCard> topRow,
        List<TribeCard> bottomRow,
        List<BuildingCard> topBuildings,
        List<BuildingCard> bottomBuildings,
        Map<Era, List<BuildingCard>> buildingsDecks,
        List<TileSlotSnapshot> turnOrderTileSlots,
        List<TileSlotSnapshot> offerTrack
) {}