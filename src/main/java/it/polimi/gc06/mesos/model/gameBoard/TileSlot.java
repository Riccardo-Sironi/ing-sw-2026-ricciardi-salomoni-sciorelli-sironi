package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;

public class TileSlot {

    private Player player;
    private TileEffect tileEffect;

    public TileSlot(){
        this.tileEffect = null;
        this.player = null;
    }

    /**
     * Returns the player currently on the tile.
     *
     * @return the element at the specified position in this list
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Set the player on the tile and applies the effect.
     *
     * @param player the player placed on the tile.
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws IllegalStateException    {@inheritDoc}
     */
    public void setPlayer(Player player) throws IllegalArgumentException, IllegalStateException {
        if (player == null) throw new IllegalArgumentException();
        if (this.player != null) throw new IllegalStateException();
        this.player = player;
    }

    /**
     * Set the effect on the tile.
     * This method should be called only during initialization.
     *
     * @param tileEffect the tile effect.
     */
    public void setTileEffect(TileEffect tileEffect) {
        this.tileEffect = tileEffect;
    }

    /**
     * Gets the effect on the tile.
     *
     * @return the tile effect.
     */
    public TileEffect getTileEffect(){
        return tileEffect;
    }

    /**
     * Apply the effects on the player, it needs to have a player or an IllegalArgumentException gets thrown.
     * If a null effect is loaded nothing happens.
     *
     * @throws IllegalStateException {@inheritDoc}
     */

    public void applyEffect() throws IllegalStateException {
        if (player == null) throw new IllegalStateException();
        if (tileEffect != null) tileEffect.execute(player);
    }

    /**
     * Removes the player from the tile, it needs to have a player or an IllegalArgumentException gets thrown.
     *
     * @return the player removed from the tile.
     * @throws IllegalStateException {@inheritDoc}
     */
    public Player removePlayer() throws IllegalStateException {
        if (player == null) throw new IllegalStateException();
        Player p = player;
        player = null;
        return p;
    }

    /**
     * Returns true if the tile has no player on it, false otherwise.
     *
     * @return whether the tile has a reference to a player or not
     * @throws IllegalStateException {@inheritDoc}
     */
    public boolean isEmpty() {
        return player == null;
    }

}
