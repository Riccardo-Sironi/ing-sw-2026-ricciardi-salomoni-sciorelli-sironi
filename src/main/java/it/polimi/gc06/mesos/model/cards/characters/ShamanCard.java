package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.CardVisitor;

import java.util.Objects;

public class ShamanCard extends CharacterCard {
    private int nStars;

    public ShamanCard() {
        super();
        this.nStars = -1;
    }

    /**
     * For testing purpose only!
     */
    public ShamanCard(Era era, int nStars) {
        super(era);
        this.nStars = nStars;
    }

    /**
     * Number of star setter. This should be called only once during initialization.
     *
     * @param nStars the number of star on the shaman card.
     */
    private void setnStars(int nStars) {
        this.nStars = nStars;
    }

    /**
     * This method is used to know how many stars the shaman card has.
     *
     * @return the integer number of stars of the shaman card
     */
    public int getnStars() {
        return nStars;
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
     * this method compares this ShamanCard to the specified object.
     * Two ShamanCards are considered equal if they provide the same number of stars.
     *
     * @param o the reference object with which to compare.
     * @return true if this object provides the same number of stars as the argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShamanCard that = (ShamanCard) o;
        return nStars == that.nStars;
    }


    /**
     * this method calculates the hash code of the ShamanCard based on its number of stars.
     *
     * @return the hash code of the ShamanCard.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(nStars);
    }
}
