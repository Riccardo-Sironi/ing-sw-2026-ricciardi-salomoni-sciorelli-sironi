package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.cards.events.HuntEvent;
import it.polimi.gc06.mesos.model.cards.events.PaintingsEvent;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;

public class HunterFoodIconVisitor implements TribeCardVisitor {

    private final Player player;

    public HunterFoodIconVisitor(Player player) {
        this.player = player;
    }

    @Override
    public void visit(RitualEvent ritual) {

    }

    @Override
    public void visit(SustenanceEvent sustenance) {

    }

    @Override
    public void visit(HuntEvent hunt) {

    }

    @Override
    public void visit(PaintingsEvent paintings) {

    }

    @Override
    public void visit(HunterCard card) {
        if (card.hasFoodIcon()) {
            player.addFoodTokens(player.getCharacterDeck().get(CharacterType.HUNTER).size());
        }
    }

    @Override
    public void visit(ShamanCard card) {
    }

    @Override
    public void visit(ArtistCard card) {
    }

    @Override
    public void visit(BuilderCard card) {
    }

    @Override
    public void visit(InventorCard card) {
    }

    @Override
    public void visit(GathererCard card) {
    }

    @Override
    public void visit(TribeCard card) {

    }
}
