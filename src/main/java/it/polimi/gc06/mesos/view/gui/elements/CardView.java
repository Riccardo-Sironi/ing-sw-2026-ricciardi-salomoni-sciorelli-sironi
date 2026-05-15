package it.polimi.gc06.mesos.view.gui.elements;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.gui.helpers.EffectsManager;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class CardView extends ImageView {

    Card card;

    public CardView(Image image) {
        super(image);
        this.setPreserveRatio(true);
        this.setSmooth(true);

        EffectsManager.normalCard(this);
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }
}
