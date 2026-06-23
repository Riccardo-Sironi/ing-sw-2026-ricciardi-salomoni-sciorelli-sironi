package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents the Sustenance Event card.
 * Players must pay food to feed their tribe, and lose prestige for any unfed characters.
 */
public class SustenanceEvent extends EventCard {

    private static final int defaultGathererDiscount = 3;
    private int numPrestigeLoss;

    /**
     * Constructs a default SustenanceEvent.
     */
    public SustenanceEvent() {
        super(true);
        numPrestigeLoss = -1;
    }

    /**
     * Constructs a SustenanceEvent with specified parameters.
     *
     * @param era The era of the event card.
     * @param prestigeLoss The prestige lost for each unfed character.
     */
    public SustenanceEvent(Era era, int prestigeLoss) {
        super(true, era);
        this.numPrestigeLoss = prestigeLoss;
    }

    /**
     * Prestige lost setter. This should be called only once during initialization.
     *
     * @param prestigeLoss The prestige lost if the player loses the event.
     */
    public void setPrestigeLoss(int prestigeLoss) {
        this.numPrestigeLoss = prestigeLoss;
    }

    /**
     * Prestige lost getter
     *
     * @return The prestige lost if the player loses the event.
     */
    public int getPrestigeLoss() {
        return this.numPrestigeLoss;
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
     * it gets the total number of character cards of the player and initialize the gatherers counter
     * and the required food. if the required food is greater than 0 it initializes the current food
     * and if the player has enough food to pay it removes it; otherwise it removes food and initialize the
     * unfed characters (remaining food that needs to be paid) and removes the prestige written on the card
     * multiplied by the unfed characters.
     *
     * @param player The player that is resolving the event.
     */
    @Override
    public void resolveEvent(Player player) {

        // get total number of character cards of the player
        int requiredFood = getRequiredFood(player);

        if (requiredFood > 0) {

            // initialization of current food (food player has)
            int currentFood = player.getFoodTokens();

            // if player has enough food to pay remove it
            if (currentFood >= requiredFood) {
                player.removeFoodTokens(requiredFood);
            }

            // otherwise remove food and...
            else {
                player.removeFoodTokens(currentFood);

                // ...initialize unfed characters (remaining food that needs to be paid)
                int unpaidFood = requiredFood - currentFood;

                // remove the prestige written on the card multiplied by the unfed characters
                player.removePrestigeTokens(unpaidFood * numPrestigeLoss);
            }
        }
    }

    /**
     * Calculates the amount of food required to feed the player's tribe,
     * considering any active discounts or specific building effects.
     *
     * @param player The player whose food requirement is being calculated.
     * @return The amount of food required.
     */
    private int getRequiredFood(Player player) {
        int totalCharacterCards = player.getCharacterDeck().values().stream()
                .mapToInt(ArrayList::size)
                .sum();

        // initialization gatherers count
        int gatherersCount = player.getGatherersCounter();
        // initialization artists count
        int artistsCount = player.getArtistsCounter();
        // initialization artists count
        int inventorsCount = player.getInventorsCounter();

        // initialization of required food (adding the gatherer discount)
        int requiredFood = Math.max(totalCharacterCards - (gatherersCount * defaultGathererDiscount), 0);

        if (player.getBuildingCards().contains(
                getRegistry().get(ModifierBuildingRegistryKey.SUSTENANCE_GATHERER_DISCOUNT)
        )) {
            requiredFood = Math.max((requiredFood - gatherersCount), 0);
        }
        if (player.getBuildingCards().contains(
                getRegistry().get(ModifierBuildingRegistryKey.SUSTENANCE_ARTIST_DISCOUNT)
        )) {
            requiredFood = Math.max((requiredFood - artistsCount), 0);
        }
        if (player.getBuildingCards().contains(
                getRegistry().get(ModifierBuildingRegistryKey.SUSTENANCE_INVENTOR_DISCOUNT)
        )) {
            requiredFood = Math.max((requiredFood - inventorsCount), 0);
        }
        return requiredFood;
    }


    /**
     * This method compares this SustenanceEvent to the specified object.
     * Two SustenanceEvents are considered equal if they have the same prestige loss penalty.
     *
     * @param o The reference object with which to compare.
     * @return True if this object has the same penalty as the argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SustenanceEvent that = (SustenanceEvent) o;
        return numPrestigeLoss == that.numPrestigeLoss;
    }


    /**
     * This method calculates the hash code of the SustenanceEvent based on its num of prestige loss.
     *
     * @return The hash code of the SustenanceEvent.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(numPrestigeLoss);
    }
}