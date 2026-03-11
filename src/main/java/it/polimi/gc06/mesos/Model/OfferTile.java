package it.polimi.gc06.mesos.Model;

import java.util.ArrayList;

public class OfferTile {

    private final TileSlot slot;

    /**
     * OfferTile constructor.
     * @param  effect represents the effect of the tile, can be null if no effect must be applied.
     */
    public OfferTile(TileEffect effect){
        slot = new TileSlot(effect);
    }

    /**
     * Remove the player from the tile and applies the effect, if no player is removed an IllegalStateException is thrown.
     * @param context the current GameModel
     * @return the player removed from the tile.
     * @throws IllegalStateException {@inheritDoc}
     */
    protected Player removePlayer(GameModel context) throws IllegalStateException{
        slot.applyEffect(context);
        return slot.removePlayer();
    }

    /**
     * Sets the player on the tile.
     *
     * @param player the player to add to the tile.
     * @throws IllegalArgumentException {@inheritDoc}
     */
    protected void placePlayer(Player player) throws IllegalArgumentException{
        if(player == null) throw new IllegalArgumentException();
        slot.setPlayer(player);
    }

}
