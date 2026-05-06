package it.polimi.gc06.mesos.model.cards.characters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.Era;


public class ArtistCard extends CharacterCard {

    /**
     * For testing purpose only!
     */
    public ArtistCard(Era era) {
        super(era);
    }

    public ArtistCard() {
        super();
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

    @Override
    public boolean equals(Object o){
        return getClass() == o.getClass();
    }
}
