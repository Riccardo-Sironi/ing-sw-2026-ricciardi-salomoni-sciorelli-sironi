package it.polimi.gc06.mesos.Model;

public class SustenanceEvent extends EventCard{

    private int numPrestigeLost;

    //CONSTRUCTOR
    public SustenanceEvent (String eventType, Era era, int numPrestigeLost){
        super(eventType, era);
        this.numPrestigeLost = numPrestigeLost;
    }

    @Override
    public void resolveEvent(Player player, GameModel context) {
        int totalCharacterCards = player.getCharacterDeck().size();
        int gatherersCount = 0;

        for (CharacterCard card : player.getCharacterDeck()){
            if (card instanceof GathererCard) gatherersCount++;
        }

        int requiredFood = totalCharacterCards - (gatherersCount * 3);
        if (requiredFood < 0) {
            requiredFood = 0;
        }

        int currentFood = player.getFoodTokens();

        if (currentFood >= requiredFood) player.removeFoodTokens(requiredFood);
        else {
            player.removeFoodTokens(currentFood);

            int unfedCharacters = requiredFood - currentFood;

            player.removePrestigeTokens(unfedCharacters * numPrestigeLost);
        }
    }
}
