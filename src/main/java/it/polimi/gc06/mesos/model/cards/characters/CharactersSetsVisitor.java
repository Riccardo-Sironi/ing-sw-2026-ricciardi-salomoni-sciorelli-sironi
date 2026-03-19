package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.events.HuntEvent;
import it.polimi.gc06.mesos.model.cards.events.PaintingsEvent;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.Player;

public class CharactersSetsVisitor implements TribeCardVisitor {
    private final Player player;

    /**
     * CharactersSetsVisitor
     * this method is used to create a visitor that will update the characters sets of the player.
     *
     * @param player the player that will be visited, this visitor will update the characters sets of the player.
     */
    public CharactersSetsVisitor(Player player) {
        this.player = player;
    }

    /**
     * this method should not be used in this implementation.
     */
    @Override
    public void visit(RitualEvent ritual) {}

    /**
     * this method should not be used in this implementation.
     */
    @Override
    public void visit(SustenanceEvent sustenance) {}

    /**
     * this method should not be used in this implementation.
     */
    @Override
    public void visit(HuntEvent hunt) {}

    /**
     * this method should not be used in this implementation.
     */
    @Override
    public void visit(PaintingsEvent paintings) {}

    /**
     * this method is used to update the number of HUNTERS to the player characters sets.
     *
     * @param card the card that will be visited.
     */
    @Override
    public void visit(HunterCard card) {
        player.getCharactersSets().put(CharacterType.HUNTER, player.getCharactersSets().get(CharacterType.HUNTER) + 1);
    }

    /**
     * this method is used to update the number of SHAMANS to the player characters sets.
     *
     * @param card the card that will be visited.
     */
    @Override
    public void visit(ShamanCard card) {
        player.getCharactersSets().put(CharacterType.SHAMAN, player.getCharactersSets().get(CharacterType.SHAMAN) + 1);
    }

    /**
     * this method is used to update the number of ARTISTS to the player characters sets.
     *
     * @param card the card that will be visited.
     */
    @Override
    public void visit(ArtistCard card) {
        player.getCharactersSets().put(CharacterType.ARTIST, player.getCharactersSets().get(CharacterType.ARTIST) + 1);
    }

    /**
     * this method is used to update the number of BUILDER to the player characters sets.
     *
     * @param card the card that will be visited.
     */
    @Override
    public void visit(BuilderCard card) {
        player.getCharactersSets().put(CharacterType.BUILDER, player.getCharactersSets().get(CharacterType.BUILDER) + 1);
    }

    /**
     * this method is used to update the number of INVENTOR to the player characters sets.
     *
     * @param card the card that will be visited.
     */
    @Override
    public void visit(InventorCard card) {
        player.getCharactersSets().put(CharacterType.INVENTOR, player.getCharactersSets().get(CharacterType.INVENTOR) + 1);
    }

    /**
     * this method is used to update the number of GATHERERS to the player characters sets.
     *
     * @param card the card that will be visited.
     */
    @Override
    public void visit(GathererCard card) {
        player.getCharactersSets().put(CharacterType.GATHERER, player.getCharactersSets().get(CharacterType.GATHERER) + 1);
    }

    /**
     * this is a fallback method for the default case, it should never be called.
     *
     * @param card the card that will be visited.
     */
    @Override
    public void visit(TribeCard card) {
        throw new IllegalStateException("This method should never be called, it is a fallback method for the default case.");
    }
}
