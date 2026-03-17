package it.polimi.gc06.mesos.Model.Cards.Characters;

import it.polimi.gc06.mesos.Model.Cards.TribeCard;
import it.polimi.gc06.mesos.Model.Era;

public abstract class CharacterCard extends TribeCard {
    public CharacterCard(Era era) {
        super(era);
    }

    // we need this to initialize the bottom row of the board
    @Override
    public boolean isEventCard() {
        return false;
    }
}
