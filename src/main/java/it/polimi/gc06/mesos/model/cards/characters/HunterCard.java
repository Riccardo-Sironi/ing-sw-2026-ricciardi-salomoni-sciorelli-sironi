package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.CardVisitor;

import java.util.Objects;

public class HunterCard extends CharacterCard {
    private boolean hasFoodIcon;

    public HunterCard() {
        super();
        this.hasFoodIcon = false;
    }

    /**
     * For testing purpose only!
     */
    public HunterCard(Era era, boolean hasFoodIcon) {
        super(era);
        this.hasFoodIcon = hasFoodIcon;
    }

    /**
     * Food icon setter. This should be called only once during initialization.
     *
     * @param hasFoodIcon must be true if the hunter has the food icon.
     */
    public void setHasFoodIcon(boolean hasFoodIcon) {
        this.hasFoodIcon = hasFoodIcon;
    }

    /**
     * This method is used to know if the card has a food icon or not. Food icon means that the card gives a food point
     * to the player based on the number of hunters the player has (either with or without icon).
     *
     * @return true if the card has a food icon, false otherwise.
     */
    public boolean hasFoodIcon() {
        return hasFoodIcon;
    }

    /**
     * this method is used to accept a visitor that will visit the card and apply
     * the effects of the card on the player that has chosen to resolve it.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(CardVisitor visitor) {
        visitor.visit(this);
    }


    /**
     * this method compares this HunterCard to the specified object.
     * since the only attribute of the HunterCard is the boolean hasFoodIcon,
     * two HunterCards are considered equal if they have the same value for hasFoodIcon.
     *
     * @param o the reference object with which to compare.
     * @return true if the given object is exactly of the same class, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HunterCard that = (HunterCard) o;
        return hasFoodIcon == that.hasFoodIcon;
    }


    /**
     * this method is used to calculate the hash code of the HunterCard object,
     * based on the hasFoodIcon attribute.
     *
     * @return the hash code of the HunterCard object.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(hasFoodIcon);
    }
}
