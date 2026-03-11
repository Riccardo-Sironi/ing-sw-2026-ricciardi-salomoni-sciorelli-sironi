package it.polimi.gc06.mesos.Model;

public interface TileEffect {

    /**
     * Applies the effect to the player.
     *
     * @param player the player to which the effect will be applied.
     * @param context the current GameModel.
     * @return whether the effect has been applied successfully to the player
     * @throws IllegalArgumentException {@inheritDoc}
     */
    abstract boolean execute(Player player, GameModel context) throws IllegalArgumentException;
}
