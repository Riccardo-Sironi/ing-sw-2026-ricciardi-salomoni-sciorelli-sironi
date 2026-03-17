package it.polimi.gc06.mesos.Model.Cards.Buildings;

import it.polimi.gc06.mesos.Model.Era;

//per gestione eventi + carta che ti fa pescare da sopra nella fase finale + carta del posizionamento totem
public class ModifierBuildingCard extends BuildingCard {

    ModifierBuildingCard(Era era, int foodCost, int prestigeGained) {
        super(era, foodCost, prestigeGained);
    }

}