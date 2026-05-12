package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.cards.CardVisitor;

import java.util.Objects;

//per gestione eventi + carta che ti fa pescare da sopra nella fase finale + carta del posizionamento totem
public class ModifierBuildingCard extends BuildingCard {

    private ModifierBuildingRegistryKey cardKey;

    public ModifierBuildingCard() {
        super();
        cardKey = null;
    }

    /**
     * cardKey setter. This should be called only once during initialization.
     *
     * @param cardKey the era of the card.
     * @throws IllegalStateException    gets thrown if this setter is called more than once.
     * @throws IllegalArgumentException if the era is null
     */
    public void setCardKey(ModifierBuildingRegistryKey cardKey) throws IllegalStateException, IllegalArgumentException {
        if (cardKey == null) throw new IllegalArgumentException();
        if (this.cardKey != null) throw new IllegalStateException("Setter has been already called");
        this.cardKey = cardKey;
    }

    /**
     * cardKey getter.
     *
     * @return the key needed for the registry.
     */
    public ModifierBuildingRegistryKey getCardKey() {
        return cardKey;
    }

    /**
     * this method is used to accept a visitor that will visit the card and apply
     * the effects of the card on the player that has chosen to resolve it.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(CardVisitor visitor) {visitor.visit(this);}

    /**
     * this method is used to compare two ModifierBuildingCard objects.
     * it returns true if the two objects are the same (i.e. they have the same cardKey), false otherwise.
     *
     * @param o the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ModifierBuildingCard that = (ModifierBuildingCard) o;
        return cardKey == that.cardKey;
    }

    /**
     * this method is used to calculate the hash code of the ModifierBuildingCard object,
     * based on the cardKey.
     *
     * @return the code of the ModifierBuildingCard object.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(cardKey);
    }
}