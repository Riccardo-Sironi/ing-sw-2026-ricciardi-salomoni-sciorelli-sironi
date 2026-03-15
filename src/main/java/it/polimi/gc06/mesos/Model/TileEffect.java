package it.polimi.gc06.mesos.Model;

public interface TileEffect {

    /**
     * Applies the effect to the player.
     *
     * @param player the player to which the effect will be applied.
     * @throws IllegalArgumentException {@inheritDoc}
     */
    abstract void execute(Player player) throws IllegalArgumentException;
}
