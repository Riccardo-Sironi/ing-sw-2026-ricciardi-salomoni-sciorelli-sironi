package it.polimi.gc06.mesos.view.gui.elements;

import it.polimi.gc06.mesos.model.cards.Card;
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

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setOffsetX(4);
        dropShadow.setOffsetY(4);
        dropShadow.setColor(Color.color(0, 0, 0, 0.5));
        this.setEffect(dropShadow);
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }
}
