package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.Era;

public class GathererCard extends CharacterCard {
    public GathererCard(Era era) {
        super(era);
    }

    @Override
    public void accept(TribeCardVisitor visitor) {
        visitor.visit(this);
    }
}
