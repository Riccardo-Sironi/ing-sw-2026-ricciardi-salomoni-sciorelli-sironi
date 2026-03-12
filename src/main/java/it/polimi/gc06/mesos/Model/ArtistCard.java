package it.polimi.gc06.mesos.Model;

public class ArtistCard extends CharacterCard{
    Era era;

    public ArtistCard(Era era) {
        super(era);
    }

    @Override
    protected CharacterType getCharacterType() {
        return CharacterType.ARTIST;
    }
}
