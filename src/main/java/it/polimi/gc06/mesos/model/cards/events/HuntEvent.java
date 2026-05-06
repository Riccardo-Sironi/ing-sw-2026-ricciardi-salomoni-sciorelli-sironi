package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;

public class HuntEvent extends EventCard {

    private int prestigeGain;

    public HuntEvent() {
        super(false);
        this.prestigeGain = -1;
    }

    /**
     * For testing purpose only!
     */
    public HuntEvent(Era era, int prestigeGain) {
        super(false, era);
        this.prestigeGain = prestigeGain;
    }

    /**
     * Prestige setter. This should be called only once during initialization.
     *
     * @param prestigeGain the prestige gained for each hunter.
     */
    public void setPrestigeGain(int prestigeGain) {
        this.prestigeGain = prestigeGain;
    }

    /**
     * Prestige getter.
     *
     * @return prestigeGain the prestige gained for each hunter.
     */
    public int getPrestigeGain() {
        return this.prestigeGain;
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
     * takes the number of HUNTERS for every player and if it's greater than 0 adds
     * to the player that has chosen to resolve the card as many food tokens as the number
     * of HUNTERS and as many prestige tokens as the number of HUNTERS multiplied by the prestige on the card.
     *
     * @param player the player that is resolving the event
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
}
