package it.polimi.gc06.mesos.view.gui.visitors;

import static it.polimi.gc06.mesos.view.gui.GUI.smallModel;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.*;
import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.*;
import it.polimi.gc06.mesos.view.gui.elements.CardView;
import it.polimi.gc06.mesos.view.gui.elements.EffectsManager;

public class CardEffectVisitor extends CardVisitor {

    private final CardView cardView;

    public CardEffectVisitor(CardView cardView) {
        this.cardView = cardView;
    }

    @Override
    public void visit(Card card) {
        super.visit(card);
    }

    @Override
    public void visit(TribeCard card) {
        super.visit(card);
    }

    @Override
    public void visit(EventCard card) {
        super.visit(card);
    }

    @Override
    public void visit(CharacterCard card) {
        super.visit(card);
    }

    @Override
    public void visit(BuildingCard card) {
        super.visit(card);
    }

    @Override
    public void visit(RitualEvent ritual) {
        EffectsManager.disableCard(cardView);
    }

    @Override
    public void visit(SustenanceEvent sustenance) {
        EffectsManager.disableCard(cardView);
    }

    @Override
    public void visit(HuntEvent hunt) {
        EffectsManager.disableCard(cardView);
    }

    @Override
    public void visit(PaintingsEvent paintings) {
        EffectsManager.disableCard(cardView);
    }

    @Override
    public void visit(HunterCard card) {
        if (smallModel.isActive()) {
            EffectsManager.activeCard(cardView);
        } else {
            EffectsManager.disableCard(cardView);
        }
    }

    @Override
    public void visit(ShamanCard card) {
        if (smallModel.isActive()) {
            EffectsManager.activeCard(cardView);
        } else {
            EffectsManager.disableCard(cardView);
        }
    }

    @Override
    public void visit(ArtistCard card) {
        if (smallModel.isActive()) {
            EffectsManager.activeCard(cardView);
        } else {
            EffectsManager.disableCard(cardView);
        }
    }

    @Override
    public void visit(BuilderCard card) {
        if (smallModel.isActive()) {
            EffectsManager.activeCard(cardView);
        } else {
            EffectsManager.disableCard(cardView);
        }
    }

    @Override
    public void visit(InventorCard card) {
        if (smallModel.isActive()) {
            EffectsManager.activeCard(cardView);
        } else {
            EffectsManager.disableCard(cardView);
        }
    }

    @Override
    public void visit(GathererCard card) {
        if (smallModel.isActive()) {
            EffectsManager.activeCard(cardView);
        } else {
            EffectsManager.disableCard(cardView);
        }
    }

    @Override
    public void visit(EndGameBuildingCard building) {
        if (smallModel.isActive()) {
            EffectsManager.activeCard(cardView);
        } else {
            EffectsManager.disableCard(cardView);
        }
    }

    @Override
    public void visit(ModifierBuildingCard building) {
        if (smallModel.isActive()) {
            EffectsManager.activeCard(cardView);
        } else {
            EffectsManager.disableCard(cardView);
        }
    }

    @Override
    public void visit(ObserverSetBuildingCard building) {
        if (smallModel.isActive()) {
            EffectsManager.activeCard(cardView);
        } else {
            EffectsManager.disableCard(cardView);
        }
    }

    @Override
    public void visit(ObserverPairBuildingCard building) {
        if (smallModel.isActive()) {
            EffectsManager.activeCard(cardView);
        } else {
            EffectsManager.disableCard(cardView);
        }
    }
}
