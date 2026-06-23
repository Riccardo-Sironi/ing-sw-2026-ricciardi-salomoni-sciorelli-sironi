package it.polimi.gc06.mesos.dtos.snapshots;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;

import java.util.List;
import java.util.Map;

/**
 * Represents a complete snapshot of the game's state at a specific moment.
 * Useful for saving, loading, or resuming a match.
 *
 * @param players The list of snapshots representing all players in the game.
 * @param board The snapshot of the game board.
 * @param turnManager The snapshot of the turn manager state.
 * @param tribeCardsDeck A map containing the remaining tribe cards for each era.
 * @param finalEventCards The list of event cards reserved for the final era.
 */
public record GameSnapshot(
        List<PlayerSnapshot> players,
        BoardSnapshot board,
        TurnManagerSnapshot turnManager,
        Map<Era, List<TribeCard>> tribeCardsDeck,
        List<EventCard> finalEventCards
) {
}