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

    /**
     * This method is used to know which type of character card it is.
     * @return the type of character card
     */
    protected abstract CharacterType getCharacterType();
}
