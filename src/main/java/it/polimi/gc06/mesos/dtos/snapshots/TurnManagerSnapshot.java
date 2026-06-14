package it.polimi.gc06.mesos.dtos.snapshots;

import it.polimi.gc06.mesos.model.gameTurnManager.Phase;
import java.util.List;

public record TurnManagerSnapshot(
        int round,
        int activePlayerIndex,
        List<String> playersOrderNicknames
) {}