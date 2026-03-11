package it.polimi.gc06.mesos.Model;

public abstract class CharacterCard extends TribeCard{
    private Era era;

    public CharacterCard(Era era) {
        this.era = era;
    }

    // we need this to initialize the bottom row of the board
    @Override
    public boolean isEventCard() {
        return false;
    }
}
