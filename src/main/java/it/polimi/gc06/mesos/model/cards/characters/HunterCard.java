package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.Era;

public class HunterCard extends CharacterCard {
    private final boolean hasFoodIcon;

    public HunterCard(Era era, boolean hasFoodIcon) {
        super(era);
        this.hasFoodIcon = hasFoodIcon;
    }

    /**
     * This method is used to know if the card has a food icon or not. Food icon means that the card gives a food point
     * to the player based on the number of hunters the player has (either with or without icon).
     *
     * @return true if the card has a food icon, false otherwise.
     */
    public boolean hasFoodIcon() {
        return hasFoodIcon; // TODO : ricordarsi di gestire questa logica quando implementiamo il pescaggio
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
