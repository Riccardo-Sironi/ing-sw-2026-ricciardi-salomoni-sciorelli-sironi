package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;

/**
 * Represents the abstract base class for all event cards in the game.
 * It provides common properties such as the resolution order and the connection
 * to the building modifiers' registry.
 */
public abstract class EventCard extends TribeCard {

    private final boolean lastToBeResolved;
    private transient ModifierBuildingsRegistry buildingsRegistry;

    /**
     * Constructs an EventCard with specified parameters.
     * For testing purpose only!
     *
     * @param lastToBeResolved True if the event card is the last to be resolved in the turn.
     * @param era The era of the event card.
     */
    public EventCard(boolean lastToBeResolved, Era era){
        super(era);
        this.lastToBeResolved = lastToBeResolved;
    }

    /**
     * Constructs a default EventCard.
     *
     * @param lastToBeResolved True if the event card is the last to be resolved in the turn.
     */
    public EventCard(boolean lastToBeResolved) {
        super();
        this.lastToBeResolved = lastToBeResolved;
    }

    /**
     * This method checks if the event card is the last to be resolved.
     * In particular this should return true only in the case we are dealing with the
     * sustenance event card or final event card.
     *
     * @return True if the event card is the last to be resolved in the turn, false otherwise.
     */
    public boolean isLastToBeResolved() {
        return lastToBeResolved;
    }

    /**
     * This method is used by the other event classes to resolve the event card.
     * In particular it is used to apply the effects of the event card on the player that has chosen to resolve it.
     *
     * @param player The player that is resolving the event.
     */
    public abstract void resolveEvent(Player player);


    /**
     * Sets the registry of the event card.
     *
     * @param registry The modifierBuildingCardRegistry.
     * @throws IllegalArgumentException If the registry passed is null.
     */
    public void setRegistry(ModifierBuildingsRegistry registry) throws IllegalArgumentException{
        if(registry == null) throw new IllegalArgumentException();
        buildingsRegistry = registry;
    }

    /**
     * Returns the building registry of the modifier building cards.
     * This method should only be used by cards that extends EventCard.
     *
     * @return The building registry.
     */
    protected ModifierBuildingsRegistry getRegistry(){
        return buildingsRegistry;
    }
}