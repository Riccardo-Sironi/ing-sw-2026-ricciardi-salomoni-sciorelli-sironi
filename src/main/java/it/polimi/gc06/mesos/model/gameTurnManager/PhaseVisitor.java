package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.model.cards.buildings.EndGameBuildingCard;

public interface PhaseVisitor {

    void visit(PlacingTotemPhase phase);
    void visit(EndOfRoundPhase phase);
    void visit(EventResolutionPhase phase);
    void visit(OfferResolutionPhase phase);
    void visit(Phase phase);

}
