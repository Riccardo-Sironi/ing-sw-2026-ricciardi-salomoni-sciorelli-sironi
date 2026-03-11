package it.polimi.gc06.mesos.Model;

public class BuilderCard extends CharacterCard{
    Era era;
    private int prestige;
    private int foodDiscount;

    public BuilderCard(Era era, int prestige, int foodDiscount) {
        super(era);
        this.prestige = prestige;
        this.foodDiscount = foodDiscount;
    }

    protected int getPrestige() {
        return prestige;
    }

    protected int getFoodDiscount() {
        return foodDiscount;
    }
}
