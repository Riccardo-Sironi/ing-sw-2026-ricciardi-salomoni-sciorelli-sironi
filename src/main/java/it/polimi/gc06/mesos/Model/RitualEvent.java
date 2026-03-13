package it.polimi.gc06.mesos.Model;

public class RitualEvent extends EventCard {

    private final int numPrestigeGained;
    private final int numPrestigeLost;

    //CONSTRUCTOR
    public RitualEvent(Era era, int numPrestigeGained, int numPrestigeLost) {
        super(EventType.RITUAL_EVENT, era);
        this.numPrestigeGained = numPrestigeGained;
        this.numPrestigeLost = numPrestigeLost;
    }

    /*private method that counts the total number of stars of the player*/
    private int countShamanStars(Player player) {
        int stars = 0;

        for (CharacterCard card : player.getCharacterDeck()) {
            if (card.getCharacterType() == CharacterType.SHAMAN) {
                ShamanCard shaman = (ShamanCard) card;
                stars += shaman.getStars();
            }
        }

        return stars;
    }

    @Override
    public void resolveEvent(Player player) {

        boolean hasMost = true;
        boolean hasLeast = true;

        int myStars = countShamanStars(player);

        /*for every player*/
        for (Player opponent : players) {

            /*if the nickname is the same skip to the next player (it means is the same player)*/
            if (opponent.getNickname().equals(player.getNickname())) {
                continue;
            }

            /*count the stars of the opponent player*/
            int opponentStars = countShamanStars(opponent);

            /*compare player stars with the ones of the opponent player*/
            if (myStars <= opponentStars) {
                hasMost = false;
            }
            if (myStars >= opponentStars) {
                hasLeast = false;
            }
        }

        /*if player stars are the most add prestige tokens written on the card*/
        if (hasMost) {
            player.addPrestigeTokens(numPrestigeGained);
        }
        /*if player stars are the least remove prestige tokens written on the card*/
        else if (hasLeast) {
            player.removePrestigeTokens(numPrestigeLost);
        }
        /*otherwise don't to nothing*/

    }
}
