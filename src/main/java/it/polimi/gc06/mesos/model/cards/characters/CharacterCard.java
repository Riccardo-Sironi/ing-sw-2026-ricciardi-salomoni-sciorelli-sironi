package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.Era;

public abstract class CharacterCard extends TribeCard {

    /**
     * For testing purpose only!
     */
    public CharacterCard(Era era){
        super(era);
    }

    public CharacterCard(){
        super();
    }
}
