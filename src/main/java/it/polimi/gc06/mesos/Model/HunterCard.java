package it.polimi.gc06.mesos.Model;

public class HunterCard extends CharacterCard {
    private final boolean hasFoodIcon;

    public HunterCard(Era era, boolean hasFoodIcon) {
        super(era);
        this.hasFoodIcon = hasFoodIcon;
    }

    /**
     * This method is used to know if the card has a food icon or not.
     *
     * @return true if the card has a food icon, false otherwise.
     */
    public boolean hasFoodIcon() {
        return hasFoodIcon;
    }

    @Override
    public void accept(TribeCardVisitor visitor) {
        visitor.visit(this);
    }


    @Override
    protected CharacterType getCharacterType() {
        return CharacterType.HUNTER;
    }
}
