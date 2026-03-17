package it.polimi.gc06.mesos.Model.Cards.Characters;

import it.polimi.gc06.mesos.Model.Cards.TribeCardVisitor;
import it.polimi.gc06.mesos.Model.Era;

public class GathererCard extends CharacterCard {
    public GathererCard(Era era) {
        super(era);
    }

    @Override
    public void accept(TribeCardVisitor visitor) {
        visitor.visit(this);
    }
}
