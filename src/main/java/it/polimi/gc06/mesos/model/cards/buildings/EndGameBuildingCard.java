package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;

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

    @Override
    public void accept(BuildingCardVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * this method is used to accept a visitor that will visit the card and apply
     * the effects of the card on the player that has chosen to resolve it.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(CardVisitor visitor) {visitor.visit(this);}
}
