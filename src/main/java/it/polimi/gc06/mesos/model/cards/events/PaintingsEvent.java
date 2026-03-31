package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;

public class PaintingsEvent extends EventCard {

    private final int numPrestigeGained;
    private final int numPrestigeLost;
    private final int minNumberOfArtists;

    public PaintingsEvent(Era era, int numPrestigeGained, int numPrestigeLost, int minNumberOfArtists) {
        super(era, false);
        this.numPrestigeGained = numPrestigeGained;
        this.numPrestigeLost = numPrestigeLost;
        this.minNumberOfArtists = minNumberOfArtists;
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
