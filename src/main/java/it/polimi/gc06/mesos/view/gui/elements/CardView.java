package it.polimi.gc06.mesos.view.gui.elements;

import it.polimi.gc06.mesos.model.cards.Card;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardView extends ImageView {

    Card card;

    public CardView(Image image) {
        super(image);
        this.setPreserveRatio(true);
        this.setSmooth(true);
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }
}
