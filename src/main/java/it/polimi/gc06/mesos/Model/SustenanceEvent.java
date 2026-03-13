package it.polimi.gc06.mesos.Model;

public class SustenanceEvent extends EventCard {
    private static final int DISCOUNT_PER_GATHERER = 3;
    private final int numPrestigeLoss;

    //CONSTRUCTOR
    public SustenanceEvent(Era era, int numPrestigeLoss) {
        super(EventType.SUSTENANCE_EVENT, era);
        this.numPrestigeLoss = numPrestigeLoss;
    }

    @Override
    public void resolveEvent(Player player) {

        // get total number of character cards of the player
        int totalCharacterCards = player.getCharacterDeck().size();
        // initialization gatherers count
        int gatherersCount = 0;

        // for each card of the deck
        for (CharacterCard card : player.getCharacterDeck()) {
            //if is a gatherer card increase the counter +1
            if (card.getCharacterType() == CharacterType.GATHERER) gatherersCount++;
        }

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
