package it.polimi.gc06.mesos.Model;

public class GathererCard extends CharacterCard{
    private Era era;

    public GathererCard(Era era) {
        super(era);
    }

    @Override
    protected CharacterType whatAmI() {
        return CharacterType.GHATERER;
    }
}
