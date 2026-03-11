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
        int huntersCount = 0;

        for (CharacterCard card : player.getCharacterDeck()){
            if (card instanceof HunterCard) huntersCount++;
        }

        player.addFoodTokens(huntersCount);

        player.addPrestigeTokens(huntersCount * numPrestigeGained);
    }
}
