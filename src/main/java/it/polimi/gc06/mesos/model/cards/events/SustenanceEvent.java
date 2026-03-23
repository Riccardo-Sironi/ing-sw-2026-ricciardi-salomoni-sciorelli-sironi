package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;

public class SustenanceEvent extends EventCard {

    private static final int defaultGathererDiscount = 3;
    private final int numPrestigeLoss;

    private final ModifierBuildingCard gathererDiscountCard;
    private final ModifierBuildingCard artistDiscountCard;
    private final ModifierBuildingCard inventorDiscountCard;

    public SustenanceEvent(Era era,
                           int numPrestigeLoss,
                           ModifierBuildingCard gathererDiscountCard,
                           ModifierBuildingCard artistDiscountCard,
                           ModifierBuildingCard inventorDiscountCard)
    {
        super(era, true);
        this.numPrestigeLoss = numPrestigeLoss;
        this.gathererDiscountCard = gathererDiscountCard;
        this.artistDiscountCard = artistDiscountCard;
        this.inventorDiscountCard = inventorDiscountCard;
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
     * it gets the total number of character cards of the player and initialize the gatherers counter
     * and the required food. if the required food is greater than 0 it initializes the current food
     * and if the player has enough food to pay it removes it; otherwise it removes food and initialize the
     * unfed characters (remaining food that needs to be paid) and removes the prestige written on the card
     * multiplied by the unfed characters.
     *
     * @param player the player that is resolving the event
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

    private int getRequiredFood(Player player) {
        int totalCharacterCards = player.getCharacterDeck().size();

        // initialization gatherers count
        int gatherersCount = player.getGatherersCounter();
        // initialization artists count
        int artistsCount = player.getArtistsCounter();
        // initialization artists count
        int inventorsCount = player.getInventorsCounter();

        // initialization of required food (adding the gatherer discount)
        int requiredFood = Math.max(totalCharacterCards - (gatherersCount * defaultGathererDiscount), 0);

        if (player.getBuildingCards().contains(gathererDiscountCard)) {
            requiredFood = Math.max((requiredFood - gatherersCount), 0);
        }
        if (player.getBuildingCards().contains(artistDiscountCard)){
            requiredFood = Math.max((requiredFood - artistsCount), 0);
        }
        if (player.getBuildingCards().contains(inventorDiscountCard)) {
            requiredFood = Math.max((requiredFood - inventorsCount), 0);
        }
        return requiredFood;
    }
}
