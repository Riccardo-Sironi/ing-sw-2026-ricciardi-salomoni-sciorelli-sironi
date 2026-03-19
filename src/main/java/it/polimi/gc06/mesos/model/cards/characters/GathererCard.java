package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.Era;

public class GathererCard extends CharacterCard {
    public GathererCard(Era era) {
        super(era);
    }

    /**
     * this method is used to accept a visitor that will visit
     * the card and do some operations on it, depending on the type of visitor.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(TribeCardVisitor visitor) {
        visitor.visit(this);
    }
}
