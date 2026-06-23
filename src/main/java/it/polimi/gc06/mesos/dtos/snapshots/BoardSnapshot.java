package it.polimi.gc06.mesos.dtos.snapshots;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import java.util.List;
import java.util.Map;

/**
 * Represents a static snapshot of the game board's state.
 *
 * @param currentEra The current era of the game.
 * @param isEndGame True if the game has reached the end game phase.
 * @param topRow The list of cards currently in the top row.
 * @param bottomRow The list of cards currently in the bottom row.
 * @param topBuildings The list of building cards currently in the top row.
 * @param bottomBuildings The list of building cards currently in the bottom row.
 * @param buildingsDecks A map containing the remaining building cards for each era.
 * @param turnOrderTileSlots The state of the slots on the turn order tile.
 * @param offerTrack The state of the slots on the offer track.
 */
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