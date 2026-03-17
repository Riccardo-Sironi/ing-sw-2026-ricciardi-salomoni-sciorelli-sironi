package it.polimi.gc06.mesos.Model;

public class BuilderCard extends CharacterCard {
    private final int prestige;
    private final int foodDiscount;

    @Override
    public void accept(TribeCardVisitor visitor) {
        visitor.visit(this);
    }

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
