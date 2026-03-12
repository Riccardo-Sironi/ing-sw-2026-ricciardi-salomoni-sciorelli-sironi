package it.polimi.gc06.mesos.Model;

public class BuilderCard extends CharacterCard{
    Era era;
    private final int prestige;
    private final int foodDiscount;

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

    @Override
    protected CharacterType whatAmI() {
        return CharacterType.BUILDER;
    }
}
