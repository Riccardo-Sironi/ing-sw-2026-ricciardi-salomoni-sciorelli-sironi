package it.polimi.gc06.mesos.Model;

public abstract class CharacterCard extends TribeCard{
    public CharacterCard(Era era) {
        super(era);
    }

    // we need this to initialize the bottom row of the board
    @Override
    public boolean isEventCard() {
        return false;
    }
}
