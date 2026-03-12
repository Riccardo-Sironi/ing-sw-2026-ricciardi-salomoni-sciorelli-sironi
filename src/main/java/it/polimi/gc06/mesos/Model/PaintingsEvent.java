package it.polimi.gc06.mesos.Model;

public class PaintingsEvent extends EventCard{

    private final int numPrestigeGained;
    private final int numPrestigeLost;
    private final int minNumberOfArtists;

    //CONSTRUCTOR
    public PaintingsEvent(Era era, int numPrestigeGained, int numPrestigeLost, int minNumberOfArtists){
        super(EventType.PAINTINGS_EVENT, era);
        this.numPrestigeGained = numPrestigeGained;
        this.numPrestigeLost = numPrestigeLost;
        this.minNumberOfArtists = minNumberOfArtists;
    }

    @Override
    public void resolveEvent(Player player, GameModel context) {
        /*initialization of artist counter*/
        int artistCount = 0;

        /*for each card of the deck*/
        for (CharacterCard card : player.getCharacterDeck()) {
            /*if is an artist card increase the counter +1*/
            if (card.getCharacterType() == CharacterType.ARTIST) { artistCount++;}
        }

        /*if number of artist is lower than the number of the card remove the prestige written on the card*/
        if (artistCount < minNumberOfArtists) { player.removePrestigeTokens(numPrestigeLost);}
        /*otherwise add prestige tokens for every artist in the deck multiplied by the prestige on the card*/
        else { player.addPrestigeTokens(artistCount * numPrestigeGained); }
    }
}
