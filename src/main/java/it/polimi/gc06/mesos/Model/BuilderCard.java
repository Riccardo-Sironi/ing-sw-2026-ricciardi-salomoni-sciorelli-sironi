package it.polimi.gc06.mesos.Model;

public class BuilderCard extends CharacterCard{

    private int prestige;
    private int foodDiscount;


    public BuilderCard(int prestige, int foodDiscount) {
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
