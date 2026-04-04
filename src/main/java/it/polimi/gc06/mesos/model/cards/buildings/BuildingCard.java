package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.cards.CardVisitor;

public abstract class BuildingCard implements Card {

    private Era era;
    private int foodCost;
    private int prestigeGain;

    /**
     * For testing purpose only!
     */
    public BuildingCard(Era era, int prestigeGain, int foodCost) {
        this.era = era;
        this.prestigeGain = prestigeGain;
        this.foodCost = foodCost;
    }

    public BuildingCard() {
        era = null;
        foodCost = -1;
        prestigeGain = -1;
    }

    /**
     * Era getter.
     *
     * @return the era of the building card.
     */
    public Era getEra() {
        return era;
    }

    /**
     * foodCost getter.
     *
     * @return the foodCost of the building card.
     */
    public int getFoodCost() {
        return foodCost;
    }

    /**
     * Prestige getter.
     *
     * @param owner necessary for EndGameBuildingCard
     * @return the prestige gained at the end of the game.
     */
    public int getPrestigeGain(Player owner) {
        return prestigeGain;
    }

    /**
     * Era setter. This should be called only once during initialization.
     *
     * @param era the era of the card.
     * @throws IllegalStateException    gets thrown if this setter is called more than once.
     * @throws IllegalArgumentException if the era is null
     */
    public void setEra(Era era) throws IllegalStateException, IllegalArgumentException {
        if (era == null) throw new IllegalArgumentException();
        if (this.era != null) throw new IllegalStateException("Setter has been already called");
        this.era = era;
    }

    /**
     * FoodCost setter. This should be called only once during initialization.
     *
     * @param foodCost the food cost of the card.
     * @throws IllegalStateException    gets thrown if this setter is called more than once.
     * @throws IllegalArgumentException if the food cost is negative
     */
    public void setFoodCost(int foodCost) throws IllegalStateException, IllegalArgumentException {
        if (foodCost < 0) throw new IllegalArgumentException();
        if (this.foodCost >= 0) throw new IllegalStateException("Setter has been already called");
        this.foodCost = foodCost;
    }

    /**
     * PrestigeGain setter. This should be called only once during initialization.
     *
     * @param prestigeGain the prestige gained by the card at the end of the game.
     * @throws IllegalStateException    gets thrown if this setter is called more than once.
     * @throws IllegalArgumentException if the prestigeGain is negative
     */
    public void setPrestigeGain(int prestigeGain) throws IllegalStateException, IllegalArgumentException {
        if (prestigeGain < 0) throw new IllegalArgumentException();
        if (this.prestigeGain >= 0) throw new IllegalStateException("Setter has been already called");
        this.prestigeGain = prestigeGain;
    }

    /**
     * a visitor that will perform operations on this card.
     * this method implements the double-dispatch mechanism for the Visitor pattern.
     *
     * @param visitor the visitor that will visit the card.
     */
    public abstract void accept(CardVisitor visitor);
}
