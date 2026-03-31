package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.cards.buildings.*;
import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.*;

public abstract class CardVisitor {

    //TODO teniamo le specializzazioni o tutto eredita da CardVisitor?

    public void visit(Card card){}
    public void visit(TribeCard card){}
    public void visit(EventCard card){}
    public void visit(CharacterCard card){}
    public void visit(BuildingCard card){}
    public void visit(RitualEvent ritual){}
    public void visit(SustenanceEvent sustenance){}
    public void visit(HuntEvent hunt){}
    public void visit(PaintingsEvent paintings){}
    public void visit(HunterCard card){}
    public void visit(ShamanCard card){}
    public void visit(ArtistCard card){}
    public void visit(BuilderCard card){}
    public void visit(InventorCard card){}
    public void visit(GathererCard card){}
    public void visit(EndGameBuildingCard building){}
    public void visit(ModifierBuildingCard building){}
    public void visit(ObserverSetBuildingCard building){}
    public void visit(ObserverPairBuildingCard building){}
    
}
