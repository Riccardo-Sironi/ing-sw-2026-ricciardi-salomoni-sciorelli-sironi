package it.polimi.gc06.mesos.dtos.snapshots;

import java.util.List;

public record TurnManagerSnapshot(
        int round,
        int activePlayerIndex,
        List<String> playersOrderNicknames
) {
}