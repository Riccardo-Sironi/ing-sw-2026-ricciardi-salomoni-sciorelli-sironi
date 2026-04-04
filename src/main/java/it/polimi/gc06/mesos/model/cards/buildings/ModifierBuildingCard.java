package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.cards.CardVisitor;

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

}