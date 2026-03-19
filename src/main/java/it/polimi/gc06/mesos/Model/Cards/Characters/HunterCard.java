package it.polimi.gc06.mesos.Model.Cards.Characters;

import it.polimi.gc06.mesos.Model.Cards.TribeCardVisitor;
import it.polimi.gc06.mesos.Model.Era;

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

    @Override
    public void accept(TribeCardVisitor visitor) {
        visitor.visit(this);
    }
}
