package it.polimi.gc06.mesos.Model;

import java.util.ArrayList;

public class SustenanceEvent extends EventCard {

    private final int numPrestigeLost;

    //CONSTRUCTOR
    public SustenanceEvent(Era era, int numPrestigeLost) {
        super(EventType.SUSTENANCE_EVENT, era);
        this.numPrestigeLost = numPrestigeLost;
    }

    @Override
    public void resolveEvent(Player player, ArrayList<Player> players) {
        /*initialization of the total of player's deck*/
        int totalCharacterCards = player.getCharacterDeck().size();
        /*initialization gatherers count*/
        int gatherersCount = 0;

        /*for each card of the deck*/
        for (CharacterCard card : player.getCharacterDeck()) {
            /*if is a gatherer card increase the counter +1*/
            if (card.getCharacterType() == CharacterType.GATHERER) gatherersCount++;
        }

        /*initialization of required food (adding the gatherer discount)*/
        int requiredFood = totalCharacterCards - (gatherersCount * 3);
        /*check for the required food to not be negative*/
        if (requiredFood < 0) {
            requiredFood = 0;
        }

        /*initialization of current food (food player has)*/
        int currentFood = player.getFoodTokens();

        /*if player has enough food to pay remove it*/
        if (currentFood >= requiredFood) {
            player.removeFoodTokens(requiredFood);
        }
        /*otherwise remove food and...*/
        else {
            player.removeFoodTokens(currentFood);

            /*...initialize unfed characters (remaining food that needs to be paid)*/
            int unfedCharacters = requiredFood - currentFood;

            /*remove the prestige written on the card multiplied by the unfed characters*/
            player.removePrestigeTokens(unfedCharacters * numPrestigeLost);
        }
    }
}
