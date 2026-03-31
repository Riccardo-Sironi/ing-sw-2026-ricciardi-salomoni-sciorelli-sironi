package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.Era;

public class ShamanCard extends CharacterCard {
    private final int nStars;

    public ShamanCard(Era era, int nStars) {
        super(era);
        this.nStars = nStars;
    }

    /**
     * This method is used to know how many stars the shaman card has.
     *
     * @return the integer number of stars of the shaman card
     */
    protected int getStars() {
        return nStars;
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

    /**
     * this method is used to accept a visitor that will visit the card and apply
     * the effects of the card on the player that has chosen to resolve it.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(CardVisitor visitor) {visitor.visit(this);}

}
