package it.polimi.gc06.mesos.Model;

public class ShamanCard extends CharacterCard{
    private Era era;
    private int nStars;

    public ShamanCard(Era era, int nStars) {
        super(era);
        this.nStars = nStars;
    }

    protected int getStars() {
        return nStars;
    }
}
