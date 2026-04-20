package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;

import java.util.ArrayList;

public record TurnOrderTile(ArrayList<TileSlot> slots) {

    //TODO: implement tile id for view (or the class loses significance).

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

    /**
     * Returns the player on the nth tile.
     *
     * @param n index of the element to return
     * @return the player on the nth position
     * @throws IllegalArgumentException {@inheritDoc}
     */
    protected Player getPlayerOnNthTile(int n) throws IllegalArgumentException {
        if (n < 0 || n >= slots.size()) throw new IllegalArgumentException();
        return slots.get(n).getPlayer();
    }

    /**
     * Sets the player on the nth tile.
     *
     * @param player the player to add to the nth tile.
     * @param n      index of the element to return.
     * @throws IllegalArgumentException {@inheritDoc}
     */
    protected void setPlayerOnNthTile(Player player, int n) throws IllegalArgumentException {
        if (n < 0 || n >= slots.size() || player == null) throw new IllegalArgumentException();
        slots.get(n).setPlayer(player);
    }
}
