package it.polimi.gc06.mesos.Model;

public class InventorPairVisitor implements TribeCardVisitor{

    private final Player player;

    public InventorPairVisitor(Player player) {
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

    }

    @Override
    public void visit(ArtistCard card) {

    }

    @Override
    public void visit(BuilderCard card) {

    }

    @Override
    public void visit(InventorCard card) {
        player.increaseInventorPairs(card.getIcon());
    }

    @Override
    public void visit(GathererCard card) {

    }

    @Override
    public void visit(TribeCard card) {

    }
}
