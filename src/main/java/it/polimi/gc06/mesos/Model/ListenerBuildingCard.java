package it.polimi.gc06.mesos.Model;

import java.util.function.Consumer;

//in realtà potrebbe essere tradotta come un owned building card se non ci piace il consumer
//rappresenta le due carte che ti danno cibo in base ai personaggi
public class ListenerBuildingCard extends BuildingCard{

    private Player owner;
    private final Consumer<Player> effect;

    ListenerBuildingCard(Era era, int foodCost, int prestigeGained, Consumer<Player> effect){
        super(era,foodCost,prestigeGained);
        this.effect = effect;
        owner = null;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) throws IllegalArgumentException{
        if(owner==null) throw new IllegalArgumentException();
        this.owner = owner;
    }

    public void execute(){
        if(owner != null) effect.accept(owner);
    }
}