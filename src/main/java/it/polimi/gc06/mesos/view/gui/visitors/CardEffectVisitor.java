package it.polimi.gc06.mesos.view.gui.visitors;

import static it.polimi.gc06.mesos.view.gui.GUI.smallModel;

import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.view.gui.elements.CardView;
import it.polimi.gc06.mesos.view.gui.helpers.EffectsManager;

public class CardEffectVisitor extends CardVisitor {

    private static final String PHASE_OFFER_RESOLUTION = "offer_resolution";
    private static final String PHASE_PLACING_TOTEM = "placing_totem";

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
    }

    @Override
    public void visit(EventCard card) {
        if (PHASE_OFFER_RESOLUTION.equals(smallModel.getPhase())) {
            EffectsManager.disableCard(cardView);
        } else {
            EffectsManager.normalCard(cardView);

        }
    }

    private void applyActiveEffect() {
        boolean isActive = smallModel.isActive();
        String currentPhase = smallModel.getPhase();

        if (!PHASE_OFFER_RESOLUTION.equals(currentPhase)) {
            EffectsManager.normalCard(cardView);
            return;
        }

        if (isActive && nPick > 0) {
            EffectsManager.activeCard(cardView);
        } else {
            EffectsManager.disableCard(cardView);
        }
    }
}