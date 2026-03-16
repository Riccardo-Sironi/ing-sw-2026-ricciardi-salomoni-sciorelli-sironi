package it.polimi.gc06.mesos.Model;

public interface TribeCardVisitor {

    // Events
    void visit(RitualEvent event);

    void visit(SustenanceEvent event);

    void visit(HuntEvent event);

    void visit(PaintingsEvent event);

    // Characters (even if they do nothing, we need to handle them)
    void visit(HunterCard card);

    void visit(ShamanCard card);

    void visit(ArtistCard card);

    void visit(BuilderCard card);

    void visit(InventorCard card);

    void visit(GathererCard card);

    // Fallback/Default
    void visit(TribeCard card);
}
