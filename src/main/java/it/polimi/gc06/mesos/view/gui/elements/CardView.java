package it.polimi.gc06.mesos.view.gui.elements;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.gui.helpers.EffectsManager;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static it.polimi.gc06.mesos.view.gui.GUI.imageFetcher;

public class CardView extends ImageView {

    private Card card;
    private boolean canBePicked;

    public CardView(Image image) {
        super(image);
        if (image == null) this.setImage(imageFetcher.getNullCardImage());
        this.setPreserveRatio(true);
        this.setSmooth(true);
        this.canBePicked = false;

        EffectsManager.normalCard(this);
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    public boolean canBePicked() {
        return canBePicked;
    }

    public void setCanBePicked(boolean canBePicked) {
        this.canBePicked = canBePicked;
    }
}
