package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.Era;

public abstract class CharacterCard extends TribeCard {
    public CharacterCard(Era era) {
        super(era);
    }

    /**
     * this method is used to know if the card is an event card or not.
     *
     * @return false, because character cards are not event cards.
     */
    @Override
    public boolean isEventCard() {
        return false;
    }
}
