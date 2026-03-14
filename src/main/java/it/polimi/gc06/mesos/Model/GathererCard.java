package it.polimi.gc06.mesos.Model;

public class GathererCard extends CharacterCard{
    public GathererCard(Era era) {
        super(era);
    }

    @Override
    protected CharacterType getCharacterType() {
        return CharacterType.GATHERER;
    }
}
