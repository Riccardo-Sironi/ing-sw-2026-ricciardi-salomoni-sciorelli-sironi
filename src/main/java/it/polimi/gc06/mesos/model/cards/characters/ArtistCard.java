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


    /**
     * this method compares this ArtistCard to the specified object.
     * since all instances of this specific card behave identically,
     * they are considered equal if they are of the exact same class.
     *
     * @param o the reference object with which to compare.
     * @return true if the given object is exactly of the same class.
     */
    @Override
    public boolean equals(Object o){
        return getClass() == o.getClass();
    }
}
