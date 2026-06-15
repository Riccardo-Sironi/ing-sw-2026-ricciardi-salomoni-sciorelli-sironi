package it.polimi.gc06.mesos.view.gui.visitors;

import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.view.gui.elements.CardView;
import it.polimi.gc06.mesos.view.gui.helpers.EffectsManager;

import static it.polimi.gc06.mesos.view.gui.GUI.smallModel;

public class CardEffectVisitor extends CardVisitor {

    private static final String PHASE_OFFER_RESOLUTION = "offer_resolution";
    private static final String END_OF_ROUND = "end_of_round";

    private final CardView cardView;
    private final int nPick;

    public CardEffectVisitor(CardView cardView, int nPick) {
        this.cardView = cardView;
        this.nPick = nPick;
    }

    @Override
    public void visit(CharacterCard card) {
        applyActiveEffect();
    }

    @Override
    public void visit(BuildingCard card) {
        applyActiveEffect();
        checkIfCanBePicked(card);
    }

    @Override
    public void visit(EventCard card) {
        if (smallModel.getPhase().equals(END_OF_ROUND) || smallModel.getPhase().equals(PHASE_OFFER_RESOLUTION)) {
            EffectsManager.disableCard(cardView);
        } else {
            EffectsManager.normalCard(cardView);
        }
    }

    private void applyActiveEffect() {
        boolean isActive = smallModel.isActive();
        boolean canPick = nPick > 0;
        String currentPhase = smallModel.getPhase();

        boolean isPickPhase = currentPhase.equals(END_OF_ROUND) || currentPhase.equals(PHASE_OFFER_RESOLUTION);

        if (isPickPhase && isActive && canPick) {
            EffectsManager.activeCard(cardView);
        } else if (isPickPhase && isActive) {
            EffectsManager.disableCard(cardView);
        } else {
            EffectsManager.normalCard(cardView);
        }
    }

    private void checkIfCanBePicked(BuildingCard building) {
        if (!smallModel.getPhase().equals(PHASE_OFFER_RESOLUTION) && !smallModel.getPhase().equals(END_OF_ROUND)) {
            return;
        }

        int totalCost = building.getFoodCost() - smallModel.getPlayer().getBuildersDiscount();
        if (totalCost > smallModel.getPlayer().getNumFood()) {
            EffectsManager.disableCard(cardView);
        }
    }
}