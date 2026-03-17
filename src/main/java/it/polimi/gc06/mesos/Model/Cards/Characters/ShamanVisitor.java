package it.polimi.gc06.mesos.Model.Cards.Characters;

import it.polimi.gc06.mesos.Model.Cards.Events.HuntEvent;
import it.polimi.gc06.mesos.Model.Cards.Events.PaintingsEvent;
import it.polimi.gc06.mesos.Model.Cards.Events.RitualEvent;
import it.polimi.gc06.mesos.Model.Cards.Events.SustenanceEvent;
import it.polimi.gc06.mesos.Model.Cards.TribeCard;
import it.polimi.gc06.mesos.Model.Cards.TribeCardVisitor;
import it.polimi.gc06.mesos.Model.Player;

public class ShamanVisitor implements TribeCardVisitor {

    private final Player player;

    public ShamanVisitor(Player player) {
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

    }

    @Override
    public void visit(ShamanCard card) {
        player.increaseShamanStars(card.getStars());
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
