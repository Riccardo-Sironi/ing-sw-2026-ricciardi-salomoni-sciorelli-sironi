package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.Era;

public abstract class TribeCard implements Card {
    private final Era era;

    public TribeCard(Era era) {
        this.era = era;
    }

    /**
     * This method is used to know if the card is an event card or a character card.
     *
     * @return true if the card is an event card, false if it is a character card
     */
    public abstract boolean isEventCard();

    /**
     * a visitor that will perform operations on this card.
     * this method implements the double-dispatch mechanism for the Visitor pattern.
     *
     * @param visitor the visitor that will visit the card.
     */
    public abstract void accept(TribeCardVisitor visitor);

    /**
     * This method is used to know the era of the card.
     *
     * @return the era enum value of the card
     */
    public Era getEra() {
        return era;
    }
}
