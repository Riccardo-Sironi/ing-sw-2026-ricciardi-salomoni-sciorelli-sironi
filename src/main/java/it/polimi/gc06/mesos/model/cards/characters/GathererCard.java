package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.Era;

public class GathererCard extends CharacterCard {

    /**
     * For testing purpose only!
     */
    public GathererCard(Era era) {
        super(era);
    }

    public GathererCard() {
        super();
    }

    /**
     * this method is used to accept a visitor that will visit the card and apply
     * the effects of the card on the player that has chosen to resolve it.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(CardVisitor visitor) {
        visitor.visit(this);
    }


    /**
     * this method compares this GathererCard to the specified object.
     * the result is true if the argument is not null and is a GathererCard object.
     *
     * @param o the reference object with which to compare.
     * @return true if the given object is exactly of the same class.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        return getClass() == o.getClass();
    }
}
