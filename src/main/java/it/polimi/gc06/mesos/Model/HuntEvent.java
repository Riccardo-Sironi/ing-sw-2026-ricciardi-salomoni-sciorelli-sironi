package it.polimi.gc06.mesos.Model;

public class HuntEvent extends EventCard{

    private int numPrestigeGained;

    //CONSTRUCTOR
    public HuntEvent(String eventType, Era era, int numPrestigeGained){
        super(eventType, era);
        this.numPrestigeGained = numPrestigeGained;
    }

    @Override
    public void resolveEvent(Player player, GameModel context) {
        /*initialization of hunters counter*/
        int huntersCount = 0;

        /*for each card of the deck*/
        for (CharacterCard card : player.getCharacterDeck()){
            /*if is a hunter card increase the counter +1*/
            if (card.whatAmI() == CharacterType.HUNTER) huntersCount++;
        }

        /*add food tokens for every hunter in the deck*/
        player.addFoodTokens(huntersCount);

        /*add prestige tokens for every hunter in the deck multiplied by the prestige on the card*/
        player.addPrestigeTokens(huntersCount * numPrestigeGained);
    }
}
