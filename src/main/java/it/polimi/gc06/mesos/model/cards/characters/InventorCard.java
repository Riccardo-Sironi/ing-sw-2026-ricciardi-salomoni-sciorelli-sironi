package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.Era;

import java.util.Objects;

public class InventorCard extends CharacterCard {
    private InventionIcon icon;

    public InventorCard() {
        super();
        this.icon = null;
    }

    /**
     * For testing purpose only!
     */
    public InventorCard(Era era, InventionIcon icon) {
        super(era);
        this.icon = icon;
    }

    /**
     * Icon setter. This should be called only once during initialization.
     *
     * @param icon the invention icon of the inventor.
     */
    public void setIcon(InventionIcon icon) {
        this.icon = icon;
    }

    /**
     * This method is used to know which invention icon the card has.
     *
     * @return invention icon enum value of the card.
     */
    public InventionIcon getIcon() {
        return icon;
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
     * this method compares this InventorCard to the specified object.
     * two InventorCards are considered equal if they possess the same invention icon.
     *
     * @param o the reference object with which to compare.
     * @return true if this object has the same icon as the argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InventorCard that = (InventorCard) o;
        return icon == that.icon;
    }


    /**
     * this method compares the hash code of the InventorCard based on its invention icon.
     *
     * @return the hash code of the InventorCard.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(icon);
    }
}
