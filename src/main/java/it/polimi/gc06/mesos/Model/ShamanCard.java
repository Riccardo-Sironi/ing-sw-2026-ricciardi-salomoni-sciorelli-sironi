package it.polimi.gc06.mesos.Model;

public class ShamanCard extends CharacterCard{
    private Era era;
    private final int nStars;

    public ShamanCard(Era era, int nStars) {
        super(era);
        this.nStars = nStars;
    }

    /**
     * This method is used to know how many stars the shaman card has.
     * @return the number of stars of the shaman card
     */
    protected int getStars() {
        return nStars;
    }

    @Override
    protected CharacterType getCharacterType() {
        return CharacterType.SHAMAN;
    }
}
