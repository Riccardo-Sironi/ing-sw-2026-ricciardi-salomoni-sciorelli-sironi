package it.polimi.gc06.mesos.Model;

public class InventorCard extends CharacterCard {
    private final InventionIcon icon;

    public InventorCard(Era era, InventionIcon icon) {
        super(era);
        this.icon = icon;
    }

    /**
     * This method is used to know which invention icon the card has.
     *
     * @return invention icon of the card.
     */
    public InventionIcon getIcon() {
        return icon;
    }

    @Override
    public void accept(TribeCardVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected CharacterType getCharacterType() {
        return CharacterType.INVENTOR;
    }
}
