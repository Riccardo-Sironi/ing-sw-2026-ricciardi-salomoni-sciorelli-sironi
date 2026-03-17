package it.polimi.gc06.mesos.Model.Cards.Characters;

import it.polimi.gc06.mesos.Model.Cards.Events.HuntEvent;
import it.polimi.gc06.mesos.Model.Cards.Events.PaintingsEvent;
import it.polimi.gc06.mesos.Model.Cards.Events.RitualEvent;
import it.polimi.gc06.mesos.Model.Cards.Events.SustenanceEvent;
import it.polimi.gc06.mesos.Model.Cards.TribeCard;
import it.polimi.gc06.mesos.Model.Cards.TribeCardVisitor;
import it.polimi.gc06.mesos.Model.Player;

public class CharactersSetsVisitor implements TribeCardVisitor {
    private final Player player;

    public CharactersSetsVisitor(Player player) {
        this.player = player;
    }

    @Override
    public void visit(RitualEvent ritual) {
        // Do nothing
    }

    @Override
    public void visit(SustenanceEvent sustenance) {
        // Do nothing
    }

    @Override
    public void visit(HuntEvent hunt) {
        // Do nothing
    }

    @Override
    public void visit(PaintingsEvent paintings) {
        // Do nothing
    }

    @Override
    public void visit(HunterCard card) {
        player.getCharactersSets().put(CharacterType.HUNTER, player.getCharactersSets().get(CharacterType.HUNTER) + 1);
    }

    @Override
    public void visit(ShamanCard card) {
        player.getCharactersSets().put(CharacterType.SHAMAN, player.getCharactersSets().get(CharacterType.SHAMAN) + 1);
    }

    @Override
    public void visit(ArtistCard card) {
        player.getCharactersSets().put(CharacterType.ARTIST, player.getCharactersSets().get(CharacterType.ARTIST) + 1);
    }

    @Override
    public void visit(BuilderCard card) {
        player.getCharactersSets().put(CharacterType.BUILDER, player.getCharactersSets().get(CharacterType.BUILDER) + 1);
    }

    @Override
    public void visit(InventorCard card) {
        player.getCharactersSets().put(CharacterType.INVENTOR, player.getCharactersSets().get(CharacterType.INVENTOR) + 1);
    }

    @Override
    public void visit(GathererCard card) {
        player.getCharactersSets().put(CharacterType.GATHERER, player.getCharactersSets().get(CharacterType.GATHERER) + 1);
    }

    @Override
    public void visit(TribeCard card) {
        // Do nothing, this is a fallback method for the default case, it should never be called.
        throw new IllegalStateException("This method should never be called, it is a fallback method for the default case.");
    }
}
