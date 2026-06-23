package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;

import java.util.Objects;

/**
 * Represents the Hunt Event card, which rewards the player with food and prestige
 * based on the number of hunters in their tribe.
 */
public class HuntEvent extends EventCard {

    private int prestigeGain;

    /**
     * Constructs a default HuntEvent.
     */
    public HuntEvent() {
        super(false);
        this.prestigeGain = -1;
    }

    /**
     * Constructs a HuntEvent with specified parameters.
     * For testing purpose only!
     *
     * @param era The era of the event card.
     * @param prestigeGain The prestige gained for each hunter.
     */
    public HuntEvent(Era era, int prestigeGain) {
        super(false, era);
        this.prestigeGain = prestigeGain;
    }

    /**
     * Prestige setter. This should be called only once during initialization.
     *
     * @param prestigeGain The prestige gained for each hunter.
     */
    public void setPrestigeGain(int prestigeGain) {
        this.prestigeGain = prestigeGain;
    }

    /**
     * Prestige getter.
     *
     * @return The prestige gained for each hunter.
     */
    public int getPrestigeGain() {
        return this.prestigeGain;
    }

    /**
     * This method is used to accept a visitor that will visit the card and apply
     * the effects of the card on the player that has chosen to resolve it.
     *
     * @param visitor The visitor that will visit the card.
     */
    @Override
    public void accept(CardVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * This method is used to resolve the event card:
     * takes the number of HUNTERS for every player and if it's greater than 0 adds
     * to the player that has chosen to resolve the card as many food tokens as the number
     * of HUNTERS and as many prestige tokens as the number of HUNTERS multiplied by the prestige on the card.
     *
     * @param player The player that is resolving the event.
     */
    @Override
    public void resolveEvent(Player player) {
        /*initialization of hunters counter*/
        int huntersCount = player.getHuntersCounter();

        if (huntersCount > 0) {
            /*add food tokens for every hunter in the deck*/
            player.addFoodTokens(player.getBuildingCards().contains(
                    getRegistry().get(ModifierBuildingRegistryKey.HUNT_PRESTIGE_AND_FOOD_GAIN_CARD)
            ) ? huntersCount * 2 : huntersCount);

            /*add prestige tokens for every hunter in the deck multiplied by the prestige on the card*/
            player.addPrestigeTokens(player.getBuildingCards().contains(
                    getRegistry().get(ModifierBuildingRegistryKey.HUNT_PRESTIGE_AND_FOOD_GAIN_CARD)
            ) ? (huntersCount * (prestigeGain + 1)) : huntersCount * prestigeGain);
        }
    }

    /**
     * This method compares this HuntEvent to the specified object.
     * Two HuntEvents are considered equal if they provide the same prestige gain.
     *
     * @param o The reference object with which to compare.
     * @return True if this object has the same prestige gain as the argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HuntEvent huntEvent = (HuntEvent) o;
        return prestigeGain == huntEvent.prestigeGain;
    }

    /**
     * This method calculate the hash code of the HuntEvent based on its prestige gain.
     *
     * @return The hash code of the HuntEvent.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(prestigeGain);
    }
}