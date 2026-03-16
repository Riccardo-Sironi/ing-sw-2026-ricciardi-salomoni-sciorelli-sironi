package it.polimi.gc06.mesos.Model;

public class ArtistCard extends CharacterCard {
    public ArtistCard(Era era) {
        super(era);
    }

    @Override
    public void accept(TribeCardVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected CharacterType getCharacterType() {
        return CharacterType.ARTIST;
    }
}
