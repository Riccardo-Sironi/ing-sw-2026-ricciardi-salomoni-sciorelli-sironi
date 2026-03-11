package it.polimi.gc06.mesos.Model;

public class PaintingsEvent extends EventCard{

    private int numPrestigeGained;
    private int numPrestigeLost;
    private int minNumberOfArtists;

    //CONSTRUCTOR
    public PaintingsEvent(String eventType, Era era, int numPrestigeGained, int numPrestigeLost, int minNumberOfArtists){
        super(eventType, era);
        this.numPrestigeGained = numPrestigeGained;
        this.numPrestigeLost = numPrestigeLost;
        this.minNumberOfArtists = minNumberOfArtists;
    }

    @Override
    public void resolveEvent(Player player, GameModel context) {
        int artistCount = 0;

        /*count of every artist card for each player*/
        for (CharacterCard card : player.getCharacterDeck()) {
            if (card instanceof ArtistCard) artistCount++;
        }

        if (artistCount < minNumberOfArtists) player.removePrestigeTokens(numPrestigeLost);
        else player.addPrestigeTokens(artistCount * numPrestigeGained);
    }
}
