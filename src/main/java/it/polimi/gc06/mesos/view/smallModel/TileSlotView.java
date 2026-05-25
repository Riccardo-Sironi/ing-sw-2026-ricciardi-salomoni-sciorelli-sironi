package it.polimi.gc06.mesos.view.smallModel;

import it.polimi.gc06.mesos.model.gameBoard.TileEffect;

/**
 * A lightweight client-side representation of a slot on the game board's offer track.
 * This class ties together an interactive {@link TileEffect tile effect} and the presence of a specific
 * player (typically represented as a placing totem) occupying it.
 */
public class TileSlotView {
    private PlayerView player;
    private TileEffect tileEffect;

    /**
     * Constructs a new empty TileSlotView without effect nor player.
     */
    public TileSlotView() {
        this.tileEffect = null;
        this.player = null;
    }

    /**
     * Constructs a new TileSlotView wrapping a specific TileEffect.
     *
     * @param effect the specific effect related to the game slot
     */
    public TileSlotView(TileEffect effect) {
        this.tileEffect = effect;
        this.player = null;
    }

    /**
     * Returns the player currently on the tile.
     *
     * @return the player occupying the tile, or null if the tile is empty
     */
    public PlayerView getPlayer() {
        return player;
    }

    /**
     * Set the player on the tile and applies the effect.
     *
     * @param player the player placed on the tile
     * @throws IllegalArgumentException if the provided player is null
     * @throws IllegalStateException    if a player is already structured onto this tile
     */
    public void setPlayer(PlayerView player) throws IllegalArgumentException, IllegalStateException {
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
    public TileEffect getTileEffect() {
        return tileEffect;
    }

    /**
     * Removes the player from the tile, it needs to have a player or an IllegalArgumentException gets thrown.
     *
     * @return the player removed from the tile
     * @throws IllegalStateException if this slot representation lacks an assigned player
     */
    public PlayerView removePlayer() throws IllegalStateException {
        if (player == null) throw new IllegalStateException();
        PlayerView p = player;
        player = null;
        return p;
    }

    /**
     * Returns true if the tile has no player on it, false otherwise.
     *
     * @return whether the tile has a reference to a player or not
     */
    public boolean isEmpty() {
        return player == null;
    }
}
