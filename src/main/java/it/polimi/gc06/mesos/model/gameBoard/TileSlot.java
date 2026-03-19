package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;

public class TileSlot {

    private Player player;
    private final TileEffect tileEffect;

    /**
     * TileSlot constructor.
     *
     * @param tileEffect effect applied on the player on player positioning, it can be null if no effect needs to be applied.
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public TileSlot(TileEffect tileEffect) {
        this.tileEffect = tileEffect;
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
     * Apply the effects on the player, it needs to have a player or an IllegalArgumentException gets thrown.
     *
     * @throws IllegalStateException {@inheritDoc}
     */
    public void applyEffect() throws IllegalStateException {
        if (player == null) throw new IllegalStateException();
        tileEffect.execute(player);
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

}
