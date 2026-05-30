package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;

public abstract class EventCard extends TribeCard {

    private final boolean lastToBeResolved;
    private transient ModifierBuildingsRegistry buildingsRegistry;

    /**
     * For testing purpose only!
     */
    public EventCard(boolean lastToBeResolved, Era era){
        super(era);
        this.lastToBeResolved = lastToBeResolved;
    }

    public EventCard(boolean lastToBeResolved) {
        super();
        this.lastToBeResolved = lastToBeResolved;
    }

    /**
     * This method checks if the event card is the last to be resolved, in particular this should return true
     * only in the case we  are dealing with the sustenance event card or final event card.
     *
     * @return true if the event card is the last to be resolved in the turn, false otherwise
     */
    public boolean isLastToBeResolved() {
        return lastToBeResolved;
    }

    /**
     * this method is used by the other event classes to resolve the event card,
     * in particular it is used to apply the effects of the event card on the player that has chosen to resolve it.
     *
     * @param player the player that is resolving the event
     */
    public abstract void resolveEvent(Player player);


    /**
     * sets the registry of the event card
     *
     * @param registry the modifierBuildingCardRegistry
     * @throws IllegalArgumentException if the registry passed is null
     */
    public void setRegistry(ModifierBuildingsRegistry registry) throws IllegalArgumentException{
        if(registry == null) throw new IllegalArgumentException();
        buildingsRegistry = registry;
    }

    /**
     *  Returns the building registry of the modifier building cards
     *  This method should only be used by cards that extends EventCard
     *
     * @return the building registry.
     */
    protected ModifierBuildingsRegistry getRegistry(){
        return buildingsRegistry;
    }
}