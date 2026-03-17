package it.polimi.gc06.mesos.Model.GameBoard;

import it.polimi.gc06.mesos.Model.Player;

public class RemoveFoodTileEffect implements TileEffect {

    /**
     * Remove one food from the player, if it has none it removes two prestige.
     *
     * @param player the player to which the effect will be applied.
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public void execute(Player player) throws IllegalArgumentException {
        if (player == null) throw new IllegalArgumentException();
        try {
            player.removeFoodTokens(1);
        } catch (IllegalStateException e) {
            player.removePrestigeTokens(2);
        }
    }
}
