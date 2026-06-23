package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.cards.CardVisitor;

import java.util.Objects;

/**
 * Represents a building card that modifies game rules or provides special ongoing abilities
 * to the player who owns it, identified by a specific registry key.
 */
public class ModifierBuildingCard extends BuildingCard {

    private ModifierBuildingRegistryKey cardKey;

    /**
     * Constructs a default empty ModifierBuildingCard.
     */
    public ModifierBuildingCard() {
        super();
        cardKey = null;
    }

    /**
     * CardKey setter. This should be called only once during initialization.
     *
     * @param cardKey The key of the card.
     * @throws IllegalStateException Gets thrown if this setter is called more than once.
     * @throws IllegalArgumentException If the key is null.
     */
    public void setCardKey(ModifierBuildingRegistryKey cardKey) throws IllegalStateException, IllegalArgumentException {
        if (cardKey == null) throw new IllegalArgumentException();
        if (this.cardKey != null) throw new IllegalStateException("Setter has been already called");
        this.cardKey = cardKey;
    }

    /**
     * CardKey getter.
     *
     * @return The key needed for the registry.
     */
    public ModifierBuildingRegistryKey getCardKey() {
        return cardKey;
    }

    /**
     * This method is used to accept a visitor that will visit the card and apply
     * the effects of the card on the player that has chosen to resolve it.
     *
     * @param visitor The visitor that will visit the card.
     */
    @Override
    public void accept(CardVisitor visitor) {visitor.visit(this);}

    /**
     * This method is used to compare two ModifierBuildingCard objects.
     * It returns true if the two objects are the same (i.e. they have the same cardKey), false otherwise.
     *
     * @param o The reference object with which to compare.
     * @return True if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ModifierBuildingCard that = (ModifierBuildingCard) o;
        return cardKey == that.cardKey;
    }

    /**
     * This method is used to calculate the hash code of the ModifierBuildingCard object,
     * based on the cardKey.
     *
     * @return The code of the ModifierBuildingCard object.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(cardKey);
    }
}