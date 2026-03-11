package it.polimi.gc06.mesos.Model;

public class InvetorCard extends CharacterCard{
    private Era era;
    private InventionIcon icon;

    public InvetorCard(Era era, InventionIcon icon) {
        super(era);
        this.icon = icon;
    }
}
