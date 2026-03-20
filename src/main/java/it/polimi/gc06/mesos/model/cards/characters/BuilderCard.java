package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.Era;

public class BuilderCard extends CharacterCard {
    private final int prestige;
    private final int foodDiscount;

    /**
     * this method is used to accept a visitor that will visit
     * the card and do some operations on it, depending on the type of visitor.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(TribeCardVisitor visitor) {
        visitor.visit(this);
    }

    public BuilderCard(Era era, int prestige, int foodDiscount) {
        super(era);
        this.prestige = prestige;
        this.foodDiscount = foodDiscount;
    }

    /**
     * this method is used to know how many prestige points the card gives.
     *
     * @return the integer number of prestige points the card gives
     */
    protected int getPrestige() {
        return prestige;
    }

    /**
     * this method is used to know how much food points the card gives as discount on buying buildings cards.
     *
     * @return the integer number of food points the card gives as discount
     */
    public int getFoodDiscount() {
        return foodDiscount;
    }
}
