package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.Era;

public class BuilderCard extends CharacterCard {
    private int prestige;
    private int foodDiscount;

    public BuilderCard() {
        super();
        this.prestige = -1;
        this.foodDiscount = -1;
    }

    /**
     * For testing purpose only!
     */
    public BuilderCard(Era era, int prestige, int foodDiscount) {
        super(era);
        this.prestige = prestige;
        this.foodDiscount = foodDiscount;
    }

    /**
     * Prestige setter. This should be called only once during initialization.
     *
     * @param prestige the prestige given by the builder.
     */
    public void setPrestige(int prestige) {
        this.prestige = prestige;
    }

    /**
     * Food discount setter. This should be called only once during initialization.
     *
     * @param foodDiscount the food discount given by the builder.
     */
    public void setFoodDiscount(int foodDiscount) {
        this.foodDiscount = foodDiscount;
    }

    /**
     * this method is used to accept a visitor that will visit the card and apply
     * the effects of the card on the player that has chosen to resolve it.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(CardVisitor visitor) {
        visitor.visit(this);
    }


    /**
     * this method is used to know how many prestige points the card gives.
     *
     * @return the integer number of prestige points the card gives
     */
    public int getPrestige() {
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
