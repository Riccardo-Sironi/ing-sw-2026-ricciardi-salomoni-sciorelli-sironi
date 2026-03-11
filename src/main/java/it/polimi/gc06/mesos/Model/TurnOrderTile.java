package it.polimi.gc06.mesos.Model;

import java.util.ArrayList;
import java.util.Collections;

public class TurnOrderTile {

    private final ArrayList<TileSlot> slots;

    /**
     * TurnOrderTile constructor.
     *
     * @param  slots represents the tile slots on the turn order tile
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public TurnOrderTile(ArrayList<TileSlot> slots) throws IllegalArgumentException{
        if(slots == null || slots.isEmpty()) throw new IllegalArgumentException();
        this.slots = slots;
    }

    /**
     * Returns the player on the nth tile.
     *
     * @param  n index of the element to return
     * @return the player on the nth position
     * @throws IllegalArgumentException {@inheritDoc}
     */
    protected Player getPlayerOnNthTile(int n) throws IllegalArgumentException{
        if(n < 0 || n >= slots.size()) throw new IllegalArgumentException();
        return slots.get(n).getPlayer();
    }

    /**
     * Sets the player on the nth tile.
     *
     * @param player the player to add to the nth tile.
     * @param  n index of the element to return.
     * @param context the current GameModel.
     * @return whether the effect has been applied successfully to the player placed in the specified position
     * @throws IllegalArgumentException {@inheritDoc}
     */
    protected boolean setPlayerOnNthTile(Player player, int n, GameModel context) throws IllegalArgumentException{
        if(n < 0 || n > slots.size() || player == null) throw new IllegalArgumentException();
        return slots.get(n).setPlayer(player, context);
    }
}
