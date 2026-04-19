package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.Era;

public abstract class CharacterCard extends TribeCard {

    private int minNumOfPlayers;

    /**
     * For testing purpose only!
     */
    public CharacterCard(Era era){
        super(era);
        minNumOfPlayers = 2;
    }

    public CharacterCard(){
        super();
        minNumOfPlayers = 2;
    }

    /**
     * MinPlayers setter. This should be called during initialization only!
     *
     * @param minPlayers the minimum number of player to use this card
     */
    public void setMinPlayers(int minPlayers){
        this.minNumOfPlayers = minPlayers;
    }

    /**
     * MinPlayers getter.
     *
     * @return the minimum number of player to use this card
     */
    @Override
    public int getMinPlayers(){
        return minNumOfPlayers;
    }
}
