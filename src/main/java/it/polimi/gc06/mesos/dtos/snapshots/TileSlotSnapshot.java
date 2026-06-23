package it.polimi.gc06.mesos.dtos.snapshots;

import it.polimi.gc06.mesos.model.gameBoard.TileEffect;

/**
 * Represents a static snapshot of a single tile slot on the game board.
 *
 * @param playerNickname The nickname of the player occupying the slot, or null if empty.
 * @param tileEffect The specific effect associated with this tile slot.
 */
public record TileSlotSnapshot(
        String playerNickname,
        TileEffect tileEffect
) {}