package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;

import java.util.Objects;
import java.util.function.ToIntFunction;

public class EndGameBuildingCard extends BuildingCard {

    private ToIntFunction<Player> prestigeEffect;

    /**
     * @param prestigeEffect please use standard function in enum EndGameBuildingFunction
     *                       For testing purpose only!
     */
    public EndGameBuildingCard(Era era, int prestigeGain, int foodCost, ToIntFunction<Player> prestigeEffect) {
        super(era, prestigeGain, foodCost);
        this.prestigeEffect = prestigeEffect;
    }

    public EndGameBuildingCard() {
        super();
        prestigeEffect = null;
    }

    /**
     * Prestige getter.
     *
     * @param owner the owner of the card.
     * @return the prestige gained at the end of the game.
     * @throws IllegalArgumentException if the owner is null.
     */
    @Override
    public int getPrestigeGain(Player owner) {
        if (owner == null) throw new IllegalArgumentException();
        return prestigeEffect.applyAsInt(owner) + super.getPrestigeGain(owner);
    }

    /**
     * PrestigeEffect setter. This should be called only once.
     *
     * @param prestigeEffect the prestige gained by the card at the end of the game (represented by an enum for JSON extraction).
     * @throws IllegalStateException    gets thrown if this setter is called more than once.
     * @throws IllegalArgumentException if the prestigeEffect is negative
     */
    public void setPrestigeEffect(EndGameBuildingFunction prestigeEffect) throws IllegalStateException, IllegalArgumentException {
        if (prestigeEffect == null) throw new IllegalArgumentException();
        if (this.prestigeEffect != null) throw new IllegalStateException("Setter has been already called");
        this.prestigeEffect = prestigeEffect;
    }

    /**
     * this method is used to accept a visitor that will visit the card and apply
     * the effects of the card on the player that has chosen to resolve it.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(CardVisitor visitor) {visitor.visit(this);}

    /**
     * this method is used to compare two EndGameBuildingCard objects.
     * it returns true if the two objects are equal, false otherwise.
     *
     * @param o the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EndGameBuildingCard that = (EndGameBuildingCard) o;
        return Objects.equals(prestigeEffect, that.prestigeEffect);
    }

    /**
     * this method is used to calculate the hash code of the EndGameBuildingCard object.
     *
     * @return the code of the EndGameBuildingCard object.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(prestigeEffect);
    }
}
