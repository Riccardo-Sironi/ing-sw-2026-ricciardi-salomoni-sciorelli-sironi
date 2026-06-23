package it.polimi.gc06.mesos.dtos.snapshots;

import java.util.List;

/**
 * Represents a static snapshot of the turn manager's state.
 *
 * @param round The current round number.
 * @param activePlayerIndex The index of the currently active player in the turn order.
 * @param playersOrderNicknames The ordered list of player nicknames dictating the turn order.
 */
public record TurnManagerSnapshot(
        int round,
        int activePlayerIndex,
        List<String> playersOrderNicknames
) {
}