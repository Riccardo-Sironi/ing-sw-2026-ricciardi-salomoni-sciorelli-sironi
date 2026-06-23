package it.polimi.gc06.mesos.model.cards.buildings;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.cards.CardVisitor;

/**
 * Represents the abstract base class for all building cards in the game.
 * It encapsulates the common attributes such as era, food cost, and base prestige gain.
 */
public abstract class BuildingCard implements Card {

    private Era era;
    private int foodCost;
    private int prestigeGain;

    /**
     * Constructs a BuildingCard with specified parameters.
     * For testing purpose only!
     *
     * @param era The era of the building card.
     * @param prestigeGain The base prestige gain.
     * @param foodCost The food cost to build.
     */
    public BuildingCard(Era era, int prestigeGain, int foodCost) {
        this.era = era;
        this.prestigeGain = prestigeGain;
        this.foodCost = foodCost;
    }

    /**
     * Constructs a default empty BuildingCard.
     */
    public BuildingCard() {
        era = null;
        foodCost = -1;
        prestigeGain = -1;
    }

    /**
     * Era getter.
     *
     * @return The era of the building card.
     */
    public Era getEra() {
        return era;
    }

    /**
     * FoodCost getter.
     *
     * @return The foodCost of the building card.
     */
    public int getFoodCost() {
        return foodCost;
    }

    /**
     * Prestige getter.
     *
     * @param owner Necessary for EndGameBuildingCard.
     * @return The prestige gained at the end of the game.
     */
    public int getPrestigeGain(Player owner) {
        return prestigeGain;
    }

    /**
     * Base prestige getter. Used by Jackson when saving player state.
     *
     * @return The base prestige of the card, ignoring other effects.
     */
    @JsonProperty("prestigeGain")
    public int getBasePrestigeGain() {
        return this.prestigeGain;
    }

    /**
     * Era setter. This should be called only once during initialization.
     *
     * @param era The era of the card.
     * @throws IllegalStateException Gets thrown if this setter is called more than once.
     * @throws IllegalArgumentException If the era is null.
     */
    public void setEra(Era era) throws IllegalStateException, IllegalArgumentException {
        if (era == null) throw new IllegalArgumentException();
        if (this.era != null) throw new IllegalStateException("Setter has been already called");
        this.era = era;
    }

    /**
     * FoodCost setter. This should be called only once during initialization.
     *
     * @param foodCost The food cost of the card.
     * @throws IllegalStateException Gets thrown if this setter is called more than once.
     * @throws IllegalArgumentException If the food cost is negative.
     */
    public void setFoodCost(int foodCost) throws IllegalStateException, IllegalArgumentException {
        if (foodCost < 0) throw new IllegalArgumentException();
        if (this.foodCost >= 0) throw new IllegalStateException("Setter has been already called");
        this.foodCost = foodCost;
    }

    /**
     * PrestigeGain setter. This should be called only once during initialization.
     *
     * @param prestigeGain The prestige gained by the card at the end of the game.
     * @throws IllegalStateException Gets thrown if this setter is called more than once.
     * @throws IllegalArgumentException If the prestigeGain is negative.
     */
    public void setPrestigeGain(int prestigeGain) throws IllegalStateException, IllegalArgumentException {
        if (prestigeGain < 0) throw new IllegalArgumentException();
        if (this.prestigeGain >= 0) throw new IllegalStateException("Setter has been already called");
        this.prestigeGain = prestigeGain;
    }

    /**
     * A visitor that will perform operations on this card.
     * This method implements the double-dispatch mechanism for the Visitor pattern.
     *
     * @param visitor The visitor that will visit the card.
     */
    public abstract void accept(CardVisitor visitor);

}