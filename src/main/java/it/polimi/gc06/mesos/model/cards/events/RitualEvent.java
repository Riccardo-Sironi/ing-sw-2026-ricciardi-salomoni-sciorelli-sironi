package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;

import java.util.Objects;

public class RitualEvent extends EventCard {

    private int prestigeGain;
    private int prestigeLoss;

    public RitualEvent() {
        super(false);
        this.prestigeGain = -1;
        this.prestigeLoss = -1;
    }

    /**
     * For testing purpose only!
     */
    public RitualEvent(Era era, int prestigeGain, int prestigeLoss) {
        super(false, era);
        this.prestigeGain = prestigeGain;
        this.prestigeLoss = prestigeLoss;
    }

    /**
     * Prestige gained setter. This should be called only once during initialization.
     *
     * @param prestigeGain the prestige given by the event.
     */
    public void setPrestigeGain(int prestigeGain) {
        this.prestigeGain = prestigeGain;
    }

    /**
     * Prestige lost setter. This should be called only once during initialization.
     *
     * @param prestigeLoss the prestige lost if the player loses the event.
     */
    public void setPrestigeLoss(int prestigeLoss) {
        this.prestigeLoss = prestigeLoss;
    }

    /**
     * Prestige gained getter.
     *
     * @return prestigeGain the prestige given by the event.
     */
    public int getPrestigeGain() {
        return this.prestigeGain;
    }

    /**
     * Prestige lost getter.
     *
     * @return prestigeLoss the prestige lost if the player loses the event.
     */
    public int getPrestigeLoss() {
        return this.prestigeLoss;
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
     * this method is used to resolve the event card:
     * it initializes maxStars and minStars with the maximum and minimum number of stars in the environment.
     * if the player's shaman stars are equal to the maximum awards prestige tokens
     * (doubled if they own the specific building),
     * if the player's shaman stars are equal to the minimum stars remove prestige tokens
     * (prevented if they own the specific building).
     *
     * @param player the player that is resolving the event
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RitualEvent that = (RitualEvent) o;
        return prestigeGain == that.prestigeGain && prestigeLoss == that.prestigeLoss;
    }

    @Override
    public int hashCode() {
        return Objects.hash(prestigeGain, prestigeLoss);
    }
}