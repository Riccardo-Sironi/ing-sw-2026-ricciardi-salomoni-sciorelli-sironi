package it.polimi.gc06.mesos.dtos.snapshots;

import it.polimi.gc06.mesos.model.gameBoard.TileEffect;

public record TileSlotSnapshot(
        String playerNickname,
        TileEffect tileEffect
) {}