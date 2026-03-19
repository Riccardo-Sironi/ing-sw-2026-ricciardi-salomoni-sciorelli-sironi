package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.Era;

public class InventorCard extends CharacterCard {
    private final InventionIcon icon;

    public InventorCard(Era era, InventionIcon icon) {
        super(era);
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
     * this method is used to accept a visitor that will visit
     * the card and do some operations on it, depending on the type of visitor.
     *
     * @param visitor the visitor that will visit the card.
     */
    @Override
    public void accept(TribeCardVisitor visitor) {
        visitor.visit(this);
    }
}
