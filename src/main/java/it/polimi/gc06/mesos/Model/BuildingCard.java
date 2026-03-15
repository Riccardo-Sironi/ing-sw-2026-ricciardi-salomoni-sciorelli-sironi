package it.polimi.gc06.mesos.Model;

/*IDEA GENERALE
Le building card vengono memorizzate sul Player e sul GameModel come tre diversi mazzetti in base
alla tipologia della carta (magari i mazzetti divisi a loro volta nelle ere).
Esiste comunque la classe building card per poter interagire in modo univoco con il cibo richiesto (può essere
utile durante la scelta delle carte forse) e con il prestigio guadagnato (sicramente utile per gestire a
fine partita il conteggio del prestigio)
 */


public abstract class BuildingCard {

    private final Era era;
    private final int foodCost;
    private final int prestigeGain;
    //private final BuildingEffect effect;
    //TODO: remove BuildingEffect card

    public BuildingCard(Era era, int foodCost, int prestigeGain) {
        this.era = era;
        this.foodCost = foodCost;
        this.prestigeGain = prestigeGain;
    }

    public Era getEra() {
        return era;
    }

    public int getFoodCost() {
        return foodCost;
    }

    public int getPrestigeGain() {
        return prestigeGain;
    }
}
