package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;

import java.util.Objects;

/**
 * Represents a building card that awards additional prestige points at the end of the game,
 * dynamically calculated based on a specific rule or condition related to the player's inventory.
 */
public class EndGameBuildingCard extends BuildingCard {

    private EndGameBuildingFunction prestigeEffect;

    /**
     * Constructs an EndGameBuildingCard with specified parameters.
     * For testing purpose only!
     *
     * @param era The era of the card.
     * @param prestigeGain The base prestige gain.
     * @param foodCost The food cost to build.
     * @param prestigeEffect Please use standard function in enum EndGameBuildingFunction.
     */
    public EndGameBuildingCard(Era era, int prestigeGain, int foodCost, EndGameBuildingFunction prestigeEffect) {
        super(era, prestigeGain, foodCost);
        this.prestigeEffect = prestigeEffect;
    }

    /**
     * Constructs a default empty EndGameBuildingCard.
     */
    public EndGameBuildingCard() {
        super();
        prestigeEffect = null;
    }

    /**
     * Prestige getter.
     *
     * @param owner The owner of the card.
     * @return The prestige gained at the end of the game.
     * @throws IllegalArgumentException If the owner is null.
     */
    @Override
    public int getPrestigeGain(Player owner) {
        if (owner == null) throw new IllegalArgumentException();
        return prestigeEffect.applyAsInt(owner) + super.getPrestigeGain(owner);
    }

    /**
     * PrestigeEffect setter. This should be called only once.
     *
     * @param prestigeEffect The prestige gained by the card at the end of the game (represented by an enum for JSON extraction).
     * @throws IllegalStateException Gets thrown if this setter is called more than once.
     * @throws IllegalArgumentException If the prestigeEffect is null.
     */
    public void setPrestigeEffect(EndGameBuildingFunction prestigeEffect) throws IllegalStateException, IllegalArgumentException {
        if (prestigeEffect == null) throw new IllegalArgumentException();
        if (this.prestigeEffect != null) throw new IllegalStateException("Setter has been already called");
        this.prestigeEffect = prestigeEffect;
    }

    /**
     * PrestigeEffect getter. This method used by Jackson to save the prestige effect.
     *
     * @return The prestige effect.
     */
    public EndGameBuildingFunction getPrestigeEffect(){
        return prestigeEffect;
    }

    /**
     * This method is used to accept a visitor that will visit the card and apply
     * the effects of the card on the player that has chosen to resolve it.
     *
     * @param visitor The visitor that will visit the card.
     */
    @Override
    public void accept(CardVisitor visitor) {visitor.visit(this);}

    /**
     * This method is used to compare two EndGameBuildingCard objects.
     * It returns true if the two objects are equal, false otherwise.
     *
     * @param o The reference object with which to compare.
     * @return True if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EndGameBuildingCard that = (EndGameBuildingCard) o;
        return Objects.equals(prestigeEffect, that.prestigeEffect);
    }

    /**
     * This method is used to calculate the hash code of the EndGameBuildingCard object.
     *
     * @return The code of the EndGameBuildingCard object.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(prestigeEffect);
    }
}