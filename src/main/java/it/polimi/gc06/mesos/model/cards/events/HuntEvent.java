package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;

public class HuntEvent extends EventCard {

    private final int numPrestigeGained;

    //CONSTRUCTOR
    public HuntEvent(Era era, int numPrestigeGained) {
        super(era, false);
        this.numPrestigeGained = numPrestigeGained;
    }

    @Override
    public void accept(TribeCardVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void resolveEvent(Player player) {
        /*initialization of hunters counter*/
        int huntersCount = player.getHuntersCounter();

        if (huntersCount > 0) {
            /*add food tokens for every hunter in the deck*/
            player.addFoodTokens(huntersCount);

            /*add prestige tokens for every hunter in the deck multiplied by the prestige on the card*/
            player.addPrestigeTokens(huntersCount * numPrestigeGained);
        }
    }
}
