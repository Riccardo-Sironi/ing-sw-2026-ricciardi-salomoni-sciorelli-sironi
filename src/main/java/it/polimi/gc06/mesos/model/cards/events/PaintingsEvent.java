package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;

import java.util.Objects;

/**
 * Represents the Cave Paintings Event card.
 * The player gains prestige if they have enough artists, or loses prestige if they fall below the minimum requirement.
 */
public class PaintingsEvent extends EventCard {

    private int prestigeGain;
    private int prestigeLoss;
    private int minNumberOfArtists;

    /**
     * Constructs a default PaintingsEvent.
     */
    public PaintingsEvent() {
        super(false);
        this.prestigeGain = -1;
        this.prestigeLoss = -1;
        this.minNumberOfArtists = -1;
    }

    /**
     * Constructs a PaintingsEvent with specified parameters.
     * For testing purpose only!
     *
     * @param era The era of the event card.
     * @param prestigeGain The prestige given by the event.
     * @param prestigeLoss The prestige lost if the player fails the event.
     * @param minNumberOfArtists The minimum number of artists to avoid losing the event.
     */
    public PaintingsEvent(Era era, int prestigeGain, int prestigeLoss, int minNumberOfArtists) {
        super(false, era);
        this.prestigeGain = prestigeGain;
        this.prestigeLoss = prestigeLoss;
        this.minNumberOfArtists = minNumberOfArtists;
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
     * Minimum number of artist setter. This should be called only once during initialization.
     *
     * @param minNumberOfArtists The minimum number of artists to not lose the event.
     */
    public void setMinNumberOfArtists(int minNumberOfArtists) {
        this.minNumberOfArtists = minNumberOfArtists;
    }

    /**
     * Minimum number of artist getter.
     *
     * @return The minimum number of artists to not lose the event.
     */
    public int getMinNumberOfArtists() {
        return this.minNumberOfArtists;
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
     * takes the number of ARTISTS for every player and if it's greater than the minNumberOfArtists on the card
     * remove prestige tokens from the player; otherwise add prestige tokens for every
     * artist in the deck multiplied by the prestige on the card.
     *
     * @param player The player that is resolving the event.
     */
    @Override
    public void resolveEvent(Player player) {
        /*initialization of artist counter*/
        int artistCount = player.getArtistsCounter();

        /*if number of artist is lower than the number of the card remove the prestige written on the card*/
        if (artistCount < minNumberOfArtists) {
            player.removePrestigeTokens(prestigeLoss);
        }
        /*otherwise add prestige tokens for every artist in the deck multiplied by the prestige on the card*/
        else {
            player.addPrestigeTokens(artistCount * prestigeGain);
        }

        if (player.getBuildingCards().contains(getRegistry().get(ModifierBuildingRegistryKey.PAINTING_FOOD_GAIN_CARD))) {
            player.addFoodTokens(artistCount);
        } else {
            player.addFoodTokens(0);
        }

    }


    /**
     * This method compares this PaintingsEvent to the specified object.
     * Two PaintingsEvents are considered equal if they have the same prestige gain,
     * prestige loss, and minimum number of artists required.
     *
     * @param o The reference object with which to compare.
     * @return True if this object has the same attributes as the argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PaintingsEvent that = (PaintingsEvent) o;
        return  prestigeGain == that.prestigeGain &&
                prestigeLoss == that.prestigeLoss &&
                minNumberOfArtists == that.minNumberOfArtists;
    }


    /**
     * This method calculate the hash code of the PaintingsEvent based on its prestige gain,
     * prestige loss and min number of artists.
     *
     * @return The hash code of the PaintingsEvent.
     */
    @Override
    public int hashCode() {
        return Objects.hash(prestigeGain, prestigeLoss, minNumberOfArtists);
    }
}