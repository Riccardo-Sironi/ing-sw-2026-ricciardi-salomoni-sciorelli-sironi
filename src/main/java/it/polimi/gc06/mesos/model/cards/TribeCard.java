package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.Era;

/**
 * The abstract base class for all tribe cards, which belong to a specific era.
 * This includes character cards and event cards, but excludes building cards.
 */
public abstract class TribeCard implements Card {
    private Era era;

    /**
     * Constructs a default TribeCard with no era assigned.
     */
    public TribeCard(){
        era = null;
    }

    /**
     * Constructs a TribeCard with a specific era.
     * For testing purpose only!
     *
     * @param era The era of the card.
     */
    public TribeCard(Era era){
        this.era = era;
    }

    /**
     * Era setter. This should be called only once during initialization.
     *
     * @param era The era of the card.
     */
    public void setEra(Era era) {
        this.era = era;
    }

    /**
     * A visitor that will perform operations on this card.
     * This method implements the double-dispatch mechanism for the Visitor pattern.
     *
     * @param visitor The visitor that will visit the card.
     */
    @Override
    public abstract void accept(CardVisitor visitor);

    /**
     * This method is used to know the era of the card.
     *
     * @return The era enum value of the card.
     */
    public Era getEra() {
        return era;
    }
}