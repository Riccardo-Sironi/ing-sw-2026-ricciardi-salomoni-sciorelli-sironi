package it.polimi.gc06.mesos.Model.Cards.Characters;

import it.polimi.gc06.mesos.Model.Cards.TribeCardVisitor;
import it.polimi.gc06.mesos.Model.Era;

public class ArtistCard extends CharacterCard {
    public ArtistCard(Era era) {
        super(era);
    }

    @Override
    public void accept(TribeCardVisitor visitor) {
        visitor.visit(this);
    }
}
