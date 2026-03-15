package it.polimi.gc06.mesos.Model;

//per gestione eventi + carta che ti fa pescare da sopra nella fase finale + carta del posizionamento totem
public class OwnedBuildingCard extends BuildingCard{

    private Player owner;

    OwnedBuildingCard(Era era, int foodCost, int prestigeGained){
        super(era,foodCost,prestigeGained);
        owner = null;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) throws IllegalArgumentException{
        if(owner==null) throw new IllegalArgumentException();
        this.owner = owner;
    }

    /*nell'evento/fase/slot avrai uno o più attributi:
        OwnedBuildingCard buildingCardForACondition;

    nella fase in cui vengono istanziati gli oggetti passerai nel costruttuore tutte le
    OwnedBuildingCard di interesse

    All'interno del codice di quella fase/evento/slot ti basterà fare
        if(buildingCardForACondition.getOwner().isEqual(playerOfInterest)){
            //esegui le modifiche necessarie...
        }
    */
}
