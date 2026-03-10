package it.polimi.gc06.mesos.Model;

public class ShamanCard extends CharacterCard{
    private int nStars;

    protected ShamanCard(int nStars) {
        this.nStars = nStars;
    }

    protected int getStars() {
        return nStars;
    }
}
