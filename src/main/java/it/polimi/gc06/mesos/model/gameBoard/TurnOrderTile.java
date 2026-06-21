package it.polimi.gc06.mesos.model.gameBoard;

import java.util.ArrayList;

public record TurnOrderTile(ArrayList<TileSlot> slots) {

    /**
     * TurnOrderTile constructor.
     *
     * @param slots represents the tile slots on the turn order tile
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public TurnOrderTile {
        if (slots == null) throw new IllegalArgumentException();
    }

    public void addSlot(int index, TileSlot slot) {
        slots.add(index, slot);
    }

}
