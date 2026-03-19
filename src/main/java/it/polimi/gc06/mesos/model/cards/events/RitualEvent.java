package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;

public class RitualEvent extends EventCard {

    private final int numPrestigeGained;
    private final int numPrestigeLost;
    
    private final ModifierBuildingCard noLossCard;
    private final ModifierBuildingCard doubleWinCard;

    public RitualEvent(Era era, int numPrestigeGained, int numPrestigeLost, ModifierBuildingCard noLossCard, ModifierBuildingCard doubleWinCard) {
        super(era, false);
        this.numPrestigeGained = numPrestigeGained;
        this.numPrestigeLost = numPrestigeLost;
        this.noLossCard = noLossCard;
        this.doubleWinCard = doubleWinCard;
    }

    /**
     * this method is used to accept a visitor that will visit the card and apply
     * the effects of the card on the player that has chosen to resolve it.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(TribeCardVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * this method is used to resolve the event card:
     * it initializes maxStars and minStars with the maximum and minimum number of stars in the environment.
     * if the player's shaman stars are equal to the maximum awards prestige tokens
     * (doubled if they own the specific building),
     * if the player's shaman stars are equal to the minimum stars remove prestige tokens
     * (prevented if they own the specific building).
     *
     * @param player the player that is resolving the event
     */
    @Override
    public void resolveEvent(Player player) {
        int maxStars = player.getEnvironment().getMaxStars();
        int minStars = player.getEnvironment().getMinStars();

        if (player.getShamanStars() == maxStars) {
            player.addPrestigeTokens(player.getBuildingCards().contains(doubleWinCard) ? numPrestigeGained * 2 : numPrestigeGained);
        }
        if (player.getShamanStars() == minStars) {
            player.removePrestigeTokens(player.getBuildingCards().contains(noLossCard) ? 0 : numPrestigeLost);
        }
    }
}