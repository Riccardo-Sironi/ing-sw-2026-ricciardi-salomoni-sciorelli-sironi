package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;

public class HuntEvent extends EventCard {

    private final int numPrestigeGained;

    //CONSTRUCTOR
    public HuntEvent(Era era, int numPrestigeGained) {
        super(era, false);
        this.numPrestigeGained = numPrestigeGained;
    }

    /**
     * this method is used to accept a visitor that will visit the card and apply
     * the effects of the card on the player that has chosen to resolve it.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(TribeCardVisitor visitor) {
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
            ) ? (huntersCount * (numPrestigeGained + 1)) : huntersCount * numPrestigeGained);
        }
    }
}
