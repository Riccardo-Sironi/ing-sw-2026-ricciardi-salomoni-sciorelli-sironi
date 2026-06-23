package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;

import java.util.Objects;

/**
 * Represents the Shamanic Ritual Event card.
 * Players compare their shaman stars to the environment's maximum and minimum to gain or lose prestige.
 */
public class RitualEvent extends EventCard {

    private int prestigeGain;
    private int prestigeLoss;

    /**
     * Constructs a default RitualEvent.
     */
    public RitualEvent() {
        super(false);
        this.prestigeGain = -1;
        this.prestigeLoss = -1;
    }

    /**
     * Constructs a RitualEvent with specified parameters.
     * For testing purpose only!
     *
     * @param era The era of the event card.
     * @param prestigeGain The prestige given by the event.
     * @param prestigeLoss The prestige lost if the player loses the event.
     */
    public RitualEvent(Era era, int prestigeGain, int prestigeLoss) {
        super(false, era);
        this.prestigeGain = prestigeGain;
        this.prestigeLoss = prestigeLoss;
    }

    /**
     * Prestige gained setter. This should be called only once during initialization.
     *
     * @param prestigeGain The prestige given by the event.
     */
    public void setPrestigeGain(int prestigeGain) {
        this.prestigeGain = prestigeGain;
    }

    /**
     * Prestige lost setter. This should be called only once during initialization.
     *
     * @param prestigeLoss The prestige lost if the player loses the event.
     */
    public void setPrestigeLoss(int prestigeLoss) {
        this.prestigeLoss = prestigeLoss;
    }

    /**
     * Prestige gained getter.
     *
     * @return The prestige given by the event.
     */
    public int getPrestigeGain() {
        return this.prestigeGain;
    }

    /**
     * Prestige lost getter.
     *
     * @return The prestige lost if the player loses the event.
     */
    public int getPrestigeLoss() {
        return this.prestigeLoss;
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
     * it initializes maxStars and minStars with the maximum and minimum number of stars in the environment.
     * if the player's shaman stars are equal to the maximum awards prestige tokens
     * (doubled if they own the specific building),
     * if the player's shaman stars are equal to the minimum stars remove prestige tokens
     * (prevented if they own the specific building).
     *
     * @param player The player that is resolving the event.
     */
    @Override
    public void resolveEvent(Player player) {
        int maxStars = player.getEnvironment().getMaxStars();
        int minStars = player.getEnvironment().getMinStars();

        if (player.getShamanStars() == maxStars) {

            /*player is the only one to has the max stars      &&          player has the doubleWinCard*/
            if (player.getEnvironment().getNumPlayerMaxStars() == 1 && player.getBuildingCards().contains(
                    getRegistry().get(ModifierBuildingRegistryKey.RITUAL_DOUBLE_WIN_CARD)
            )) {
                player.addPrestigeTokens(prestigeGain * 2);
            } else {
                player.addPrestigeTokens(prestigeGain);
            }

        }

        if (player.getShamanStars() == minStars) {
            player.removePrestigeTokens(player.getBuildingCards().contains(
                    getRegistry().get(ModifierBuildingRegistryKey.RITUAL_NO_LOSS_CARD)
            ) ? 0 : prestigeLoss);
        }
    }


    /**
     * This method compares this RitualEvent to the specified object.
     * Two RitualEvents are considered equal if they have the same prestige gain and loss.
     *
     * @param o The reference object with which to compare.
     * @return True if this object has the same attributes as the argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RitualEvent that = (RitualEvent) o;
        return prestigeGain == that.prestigeGain && prestigeLoss == that.prestigeLoss;
    }


    /**
     * This method calculates the hash code of the RitualEvent based on its attributes.
     *
     * @return The hash code of the RitualEvent.
     */
    @Override
    public int hashCode() {
        return Objects.hash(prestigeGain, prestigeLoss);
    }
}