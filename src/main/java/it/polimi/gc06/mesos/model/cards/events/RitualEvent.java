package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;

public class RitualEvent extends EventCard {

    private final int numPrestigeGained;
    private final int numPrestigeLost;

    public RitualEvent(Era era, int numPrestigeGained, int numPrestigeLost) {
        super(era, false);
        this.numPrestigeGained = numPrestigeGained;
        this.numPrestigeLost = numPrestigeLost;
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
     * this method is used to accept a visitor that will visit the card and apply
     * the effects of the card on the player that has chosen to resolve it.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(CardVisitor visitor) {visitor.visit(this);}

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

                /*player is the only one to has the max stars      &&          player has the doubleWinCard*/
            if(player.getEnvironment().getNumPlayerMaxStars() == 1 && player.getBuildingCards().contains(
                    getRegistry().get(ModifierBuildingRegistryKey.RITUAL_DOUBLE_WIN_CARD)
            )) {
                player.addPrestigeTokens(numPrestigeGained *2);
            } else { player.addPrestigeTokens(numPrestigeGained); }

        }

        if (player.getShamanStars() == minStars) {
            player.removePrestigeTokens(player.getBuildingCards().contains(
                    getRegistry().get(ModifierBuildingRegistryKey.RITUAL_NO_LOSS_CARD)
            ) ? 0 : numPrestigeLost);
        }
    }

}