package it.polimi.gc06.mesos.Model;

public class InvetorCard extends CharacterCard{
    private final InventionIcon icon;

    public InvetorCard(Era era, InventionIcon icon) {
        super(era);
        this.icon = icon;
    }

    /**
     * This method is used to know which invention icon the card has.
     * @return invention icon of the card.
     */
    public InventionIcon getIcon() {
        return icon;
    }

    @Override
    protected CharacterType getCharacterType() {
        return CharacterType.INVENTOR;
    }
}
