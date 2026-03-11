package it.polimi.gc06.mesos.Model;

public class TileSlot {

    private Player player;
    private final TileEffect tileEffect;

    /**
     * TileSlot constructor.
     *
     * @param  tileEffect effect applied on the player on player positioning, it can be null if no effect needs to be applied.
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public TileSlot(TileEffect tileEffect){
        this.tileEffect = tileEffect;
        this.player = null;
    }

    /**
     * Returns the player currently on the tile.
     * @return the element at the specified position in this list
     */
    protected Player getPlayer() {
        return player;
    }

    /**
     * Set the player on the tile and applies the effect.
     *
     * @param player the player placed on the tile.
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    protected void setPlayer(Player player) throws IllegalArgumentException, IllegalStateException{
        if(player == null) throw new IllegalArgumentException();
        if(this.player != null) throw new IllegalStateException();
        this.player = player;
    }

    /**
     * Apply the effects on the player, it needs to have a player or an IllegalArgumentException gets thrown.
     * @param context the current GameModel
     * @return if the effects has been applied successfully.
     * @throws IllegalStateException {@inheritDoc}
     */
    protected boolean applyEffect(GameModel context) throws IllegalArgumentException, IllegalStateException {
        if(context == null) throw new IllegalArgumentException();
        if(player == null) throw new IllegalStateException();

        return tileEffect == null || tileEffect.execute(player,context);
    }

    /**
     * Removes the player from the tile, it needs to have a player or an IllegalArgumentException gets thrown.
     *
     * @return the player removed from the tile.
     * @throws IllegalStateException {@inheritDoc}
     */
    protected Player removePlayer() throws IllegalStateException{
        if(player == null) throw new IllegalStateException();
        Player p = player;
        player = null;
        return p;
    }

}
