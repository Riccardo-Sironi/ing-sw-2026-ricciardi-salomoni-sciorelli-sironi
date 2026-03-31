package it.polimi.gc06.mesos.model.cards.buildings;

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

    @Override
    public void accept(BuildingCardVisitor visitor) {
        visitor.visit(this);
    }
}