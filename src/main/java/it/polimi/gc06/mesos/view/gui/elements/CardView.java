package it.polimi.gc06.mesos.view.gui.elements;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.gui.helpers.EffectsManager;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static it.polimi.gc06.mesos.view.gui.GUI.imageFetcher;

public class CardView extends ImageView {

    private Card card;

    public CardView(Image image) {
        super(image);
        if (image == null) this.setImage(imageFetcher.getNullCardImage());
        this.setPreserveRatio(true);
        this.setSmooth(true);

        EffectsManager.normalCard(this);
    }

    /**
     * Set the Card object associated with the relative view
     *
     * @param card the {@link Card} object to be associated with the view
     */
    public void setCard(Card card) {
        this.card = card;
    }

    /**
     * Get the Card object associated with the relative view
     *
     * @return the {@link Card} object associated with the view
     */
    public Card getCard() {
        return card;
    }
}
