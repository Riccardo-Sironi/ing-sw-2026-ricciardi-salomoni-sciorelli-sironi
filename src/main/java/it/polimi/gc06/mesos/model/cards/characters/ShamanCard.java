package it.polimi.gc06.mesos.model.cards.characters;

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

    @Override
    public void accept(TribeCardVisitor visitor) {
        visitor.visit(this);
    }
}
