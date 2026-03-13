package it.polimi.gc06.mesos.Model;

import java.util.ArrayList;

public class HuntEvent extends EventCard {

    private final int numPrestigeGained;

    //CONSTRUCTOR
    public HuntEvent(Era era, int numPrestigeGained) {
        super(EventType.HUNT_EVENT, era);
        this.numPrestigeGained = numPrestigeGained;
    }

    @Override
    public void resolveEvent(Player player, ArrayList<Player> players) {
        /*initialization of hunters counter*/
        int huntersCount = 0;

        /*for each card of the deck*/
        for (CharacterCard card : player.getCharacterDeck()) {
            /*if is a hunter card increase the counter +1*/
            if (card.getCharacterType() == CharacterType.HUNTER) huntersCount++;
        }

        /*add food tokens for every hunter in the deck*/
        player.addFoodTokens(huntersCount);

        /*add prestige tokens for every hunter in the deck multiplied by the prestige on the card*/
        player.addPrestigeTokens(huntersCount * numPrestigeGained);
    }
}
