package it.polimi.gc06.mesos.view.gui.visitors;

import static it.polimi.gc06.mesos.view.gui.GUI.smallModel;

import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.view.gui.elements.CardView;
import it.polimi.gc06.mesos.view.gui.elements.EffectsManager;

public class CardEffectVisitor extends CardVisitor {

    private final CardView cardView;

    public CardEffectVisitor(CardView cardView) {
        this.cardView = cardView;
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
        EffectsManager.disableCard(cardView);
    }

    private void applyActiveEffect() {
        if (smallModel.isActive()) {
            EffectsManager.activeCard(cardView);
        } else {
            EffectsManager.disableCard(cardView);
        }
    }
}
