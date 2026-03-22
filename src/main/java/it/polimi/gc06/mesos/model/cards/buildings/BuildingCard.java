package it.polimi.gc06.mesos.model.cards.buildings;

/* TODO IDEA GENERALE
Le building card vengono memorizzate sul Player e sul GameModel come tre diversi mazzetti in base
alla tipologia della carta (magari i mazzetti divisi a loro volta nelle ere).
Esiste comunque la classe building card per poter interagire in modo univoco con il cibo richiesto (può essere
utile durante la scelta delle carte forse) e con il prestigio guadagnato (sicuramente utile per gestire a
fine partita il conteggio del prestigio)
 */

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;

public abstract class BuildingCard {

    private final Era era;
    private final int foodCost;
    private final int prestigeGain;

    public BuildingCard(Era era, int foodCost, int prestigeGain) {
        this.era = era;
        this.foodCost = foodCost;
        this.prestigeGain = prestigeGain;
    }

    /**
     * Era getter.
     *
     * @return the era of the building card.
     */
    public Era getEra() {
        return era;
    }

    /**
     * foodCost getter.
     *
     * @return the foodCost of the building card.
     */
    public int getFoodCost() {
        return foodCost;
    }

    /**
     * Prestige getter.
     *
     * @param owner necessary for EndGameBuildingCard
     * @return the prestige gained at the end of the game.
     */
    public int getPrestigeGain(Player owner) {
        return prestigeGain;
    }

}
