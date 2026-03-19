package it.polimi.gc06.mesos.Model.Cards;

import it.polimi.gc06.mesos.Model.Era;

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
     * TODO : explain
     *
     * @param visitor
     */
    public abstract void accept(TribeCardVisitor visitor);

    /**
     * This method is used to know the era of the card.
     *
     * @return the era enum value of the card
     */
    protected Era getEra() {
        return era;
    }
}
