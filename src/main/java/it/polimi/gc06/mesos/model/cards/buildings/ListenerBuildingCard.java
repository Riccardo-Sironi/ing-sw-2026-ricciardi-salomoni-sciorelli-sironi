package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;

import java.util.function.Consumer;

//gestita esattamente come abnormalBuildingCard
//rappresenta le due carte che ti danno cibo in base ai personaggi
public class ListenerBuildingCard extends BuildingCard {

    //TODO da togliere?
    private final Consumer<Player> effect;

    ListenerBuildingCard(Era era, int foodCost, int prestigeGained, Consumer<Player> effect) {
        super(era, foodCost, prestigeGained);
        this.effect = effect;
    }

    /**
     * Execute the effect of the card.
     *
     * @param owner the owner of the card.
     */
    public void execute(Player owner) {
        if (owner != null) effect.accept(owner);
    }
}