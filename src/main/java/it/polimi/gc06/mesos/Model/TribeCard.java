package it.polimi.gc06.mesos.Model;

public abstract class TribeCard implements Card {
    Era era;

    /**
     * This method is used to know if the card is an event card or a character card.
     * @return true if the card is an event card, false if it is a character card
     */
    protected abstract boolean isEventCard();

    protected Era getEra() {
        return era;
    };
}
