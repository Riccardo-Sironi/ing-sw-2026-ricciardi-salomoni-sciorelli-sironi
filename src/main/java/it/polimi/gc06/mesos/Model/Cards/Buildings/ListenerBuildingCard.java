package it.polimi.gc06.mesos.Model.Cards.Buildings;

import it.polimi.gc06.mesos.Model.Era;
import it.polimi.gc06.mesos.Model.Player;

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

    public void execute(Player owner) {
        if (owner != null) effect.accept(owner);
    }
}