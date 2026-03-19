package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;

public class SustenanceEvent extends EventCard {

    private static final int DISCOUNT_PER_GATHERER = 3;
    private final int numPrestigeLoss;

    public SustenanceEvent(Era era, int numPrestigeLoss) {
        super(era, true);
        this.numPrestigeLoss = numPrestigeLoss;
    }

    @Override
    public void accept(TribeCardVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void resolveEvent(Player player) {

        int totalCharacterCards = player.getCharacterDeck().size();
        // get total number of character cards of the player

        // initialization gatherers count
        int gatherersCount = player.getGatherersCounter();

        // initialization of required food (adding the gatherer discount)
        int requiredFood = Math.max(totalCharacterCards - (gatherersCount * DISCOUNT_PER_GATHERER), 0);

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
}
