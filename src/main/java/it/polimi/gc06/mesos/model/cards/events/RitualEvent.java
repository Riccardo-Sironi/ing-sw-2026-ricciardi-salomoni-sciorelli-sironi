package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;

public class RitualEvent extends EventCard {

    private int numPrestigeGained;
    private int numPrestigeLost;

    public RitualEvent() {
        super(false);
        this.numPrestigeGained = -1;
        this.numPrestigeLost = -1;
    }

    /**
     * For testing purpose only!
     */
    public RitualEvent(Era era, int numPrestigeGained, int numPrestigeLost) {
        super(false, era);
        this.numPrestigeGained = numPrestigeGained;
        this.numPrestigeLost = numPrestigeLost;
    }

    /**
     * Prestige gained setter. This should be called only once during initialization.
     *
     * @param numPrestigeGained the prestige given by the event.
     */
    public void setNumPrestigeGained(int numPrestigeGained) {
        this.numPrestigeGained = numPrestigeGained;
    }

    /**
     * Prestige lost setter. This should be called only once during initialization.
     *
     * @param numPrestigeLost the prestige lost if the player loses the event.
     */
    public void setNumPrestigeLost(int numPrestigeLost) {
        this.numPrestigeLost = numPrestigeLost;
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
            if(player.getEnvironment().getNumPlayerMaxStars() == 1 && player.getBuildingCards().contains(
                    getRegistry().get(ModifierBuildingRegistryKey.RITUAL_DOUBLE_WIN_CARD)
            )) {
                player.addPrestigeTokens(numPrestigeGained *2);
            } else { player.addPrestigeTokens(numPrestigeGained); }

        }

        if (player.getShamanStars() == minStars) {
            player.removePrestigeTokens(player.getBuildingCards().contains(
                    getRegistry().get(ModifierBuildingRegistryKey.RITUAL_NO_LOSS_CARD)
            ) ? 0 : numPrestigeLost);
        }
    }

}