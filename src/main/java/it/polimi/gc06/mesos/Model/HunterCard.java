package it.polimi.gc06.mesos.Model;

public class HunterCard extends CharacterCard{
    private Era era;
    private boolean hasFoodIcon;

    public HunterCard(Era era, boolean hasFoodIcon) {
        super(era);
        this.hasFoodIcon = hasFoodIcon;
    }
}
