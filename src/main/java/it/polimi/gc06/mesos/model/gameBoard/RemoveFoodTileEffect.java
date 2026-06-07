package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;

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

    /**
     * this method is used to accept a visitor that will visit the effect and apply
     * the contextualized action of the visitor.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(TileEffectVisitor visitor) {
        visitor.visit(this);
    }
}
