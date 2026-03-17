package it.polimi.gc06.mesos.Model;

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
    protected abstract boolean isEventCard();

    public abstract void accept(TribeCardVisitor visitor);

    protected Era getEra() {
        return era;
    }
}
