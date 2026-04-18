package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;

public class PaintingsEvent extends EventCard {

    private int numPrestigeGained;
    private int numPrestigeLost;
    private int minNumberOfArtists;

    public PaintingsEvent() {
        super(false);
        this.numPrestigeGained = -1;
        this.numPrestigeLost = -1;
        this.minNumberOfArtists = -1;
    }

    /**
     * For testing purpose only!
     */
    public PaintingsEvent(Era era, int numPrestigeGained, int numPrestigeLost, int minNumberOfArtists) {
        super(false, era);
        this.numPrestigeGained = numPrestigeGained;
        this.numPrestigeLost = numPrestigeLost;
        this.minNumberOfArtists = minNumberOfArtists;
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
     * Minimum number of artist setter. This should be called only once during initialization.
     *
     * @param minNumberOfArtists the minimum number of artist to not lose the event.
     */
    public void setMinNumberOfArtists(int minNumberOfArtists) {
        this.minNumberOfArtists = minNumberOfArtists;
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
     * takes the number of ARTISTS for every player and if it's greater than the minNumberOfArtists on the card
     * remove prestige tokens from the player; otherwise add prestige tokens for every
     * artist in the deck multiplied by the prestige on the card.
     *
     * @param player the player that is resolving the event
     */
    @Override
    public void resolveEvent(Player player) {
        /*initialization of artist counter*/
        int artistCount = player.getArtistsCounter();

        /*if number of artist is lower than the number of the card remove the prestige written on the card*/
        if (artistCount < minNumberOfArtists) {
            player.removePrestigeTokens(numPrestigeLost);
        }
        /*otherwise add prestige tokens for every artist in the deck multiplied by the prestige on the card*/
        else {
            player.addPrestigeTokens(artistCount * numPrestigeGained);
        }

        if (player.getBuildingCards().contains(getRegistry().get(ModifierBuildingRegistryKey.PAINTING_FOOD_GAIN_CARD))) {
            player.addFoodTokens(artistCount);
        } else {
            player.addFoodTokens(0);
        }

    }
}
